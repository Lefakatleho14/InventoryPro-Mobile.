package com.codecrafters.inventorypro.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.codecrafters.inventorypro.databinding.ActivityRegisterBinding
import com.codecrafters.inventorypro.repository.UserRepository
import com.codecrafters.inventorypro.utils.SessionManager
import com.codecrafters.inventorypro.utils.gone
import com.codecrafters.inventorypro.utils.showToast
import com.codecrafters.inventorypro.utils.visible
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sessionManager: SessionManager
    private val userRepository = UserRepository()

    companion object {
        private const val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityRegisterBinding.inflate(layoutInflater)
            setContentView(binding.root)

            sessionManager = SessionManager(this)
            setupClickListeners()

        } catch (e: Exception) {
            Log.e(TAG, "onCreate error: ${e.message}", e)
            showToast("Error loading screen: ${e.message}")
        }
    }

    private fun setupClickListeners() {

        binding.btnRegister.setOnClickListener {
            try {
                val fullName        = binding.etFullName.text.toString().trim()
                val email           = binding.etEmail.text.toString().trim()
                val password        = binding.etPassword.text.toString().trim()
                val confirmPassword = binding.etConfirmPassword.text.toString().trim()

                if (validateInputs(fullName, email, password, confirmPassword)) {
                    registerUser(fullName, email, password)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Button click error: ${e.message}", e)
                showToast("Error: ${e.message}")
            }
        }

        binding.tvSignIn.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {

        var isValid = true

        if (fullName.isEmpty()) {
            binding.tilFullName.error = "Full name is required"
            isValid = false
        } else {
            binding.tilFullName.error = null
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Enter a valid email"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Passwords do not match"
            isValid = false
        } else {
            binding.tilConfirmPassword.error = null
        }

        return isValid
    }

    private fun registerUser(fullName: String, email: String, password: String) {
        binding.progressBar.visible()
        binding.btnRegister.isEnabled = false

        lifecycleScope.launch {
            try {
                val result = userRepository.registerUser(fullName, email, password)

                result.onSuccess { user ->
                    sessionManager.saveSession(user)
                    binding.progressBar.gone()
                    showToast("Welcome, ${user.fullName}!")
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                result.onFailure { error ->
                    binding.progressBar.gone()
                    binding.btnRegister.isEnabled = true
                    Log.e(TAG, "Registration failed: ${error.message}", error)

                    val message = when {
                        error.message?.contains("email address is already") == true ->
                            "This email is already registered. Please sign in."
                        error.message?.contains("network") == true ->
                            "Network error. Check your internet connection."
                        error.message?.contains("badly formatted") == true ->
                            "Invalid email format."
                        else ->
                            "Registration failed: ${error.message}"
                    }
                    showToast(message)
                }

            } catch (e: Exception) {
                binding.progressBar.gone()
                binding.btnRegister.isEnabled = true
                Log.e(TAG, "Exception during registration: ${e.message}", e)
                showToast("Unexpected error: ${e.message}")
            }
        }
    }
}