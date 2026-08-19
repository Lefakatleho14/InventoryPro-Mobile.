package com.codecrafters.inventorypro.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.codecrafters.inventorypro.databinding.ActivityChangePasswordBinding
import com.codecrafters.inventorypro.repository.UserRepository
import com.codecrafters.inventorypro.utils.gone
import com.codecrafters.inventorypro.utils.visible
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val userRepository = UserRepository()
    private val auth           = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Change Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnUpdatePassword.setOnClickListener { updatePassword() }
        binding.btnCancelPassword.setOnClickListener { finish() }
    }

    private fun updatePassword() {
        val current = binding.etCurrentPassword.text.toString().trim()
        val newPass  = binding.etNewPassword.text.toString().trim()
        val confirm  = binding.etConfirmPassword.text.toString().trim()

        // Validation
        if (current.isEmpty()) {
            binding.tilCurrentPassword.error = "Enter your current password"
            return
        }
        if (newPass.isEmpty() || newPass.length < 6) {
            binding.tilNewPassword.error = "New password must be at least 6 characters"
            return
        }
        if (newPass != confirm) {
            binding.tilConfirmPassword.error = "Passwords do not match"
            return
        }
        if (current == newPass) {
            binding.tilNewPassword.error = "New password must be different from current"
            return
        }

        binding.tilCurrentPassword.error  = null
        binding.tilNewPassword.error      = null
        binding.tilConfirmPassword.error  = null
        binding.progressBar.visible()
        binding.btnUpdatePassword.isEnabled = false

        // Re-authenticate then update
        val user       = auth.currentUser ?: return
        val email      = user.email ?: return
        val credential = EmailAuthProvider.getCredential(email, current)

        user.reauthenticate(credential).addOnCompleteListener { reauth ->
            if (reauth.isSuccessful) {
                lifecycleScope.launch {
                    val result = userRepository.changePassword(newPass)
                    binding.progressBar.gone()
                    result.onSuccess {
                        Toast.makeText(
                            this@ChangePasswordActivity,
                            "Password updated successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    result.onFailure {
                        binding.btnUpdatePassword.isEnabled = true
                        Toast.makeText(
                            this@ChangePasswordActivity,
                            "Failed: ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                binding.progressBar.gone()
                binding.btnUpdatePassword.isEnabled = true
                binding.tilCurrentPassword.error = "Current password is incorrect"
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}