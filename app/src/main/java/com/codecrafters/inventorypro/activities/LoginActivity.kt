package com.codecrafters.inventorypro.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.codecrafters.inventorypro.databinding.ActivityLoginBinding
import com.codecrafters.inventorypro.repository.UserRepository
import com.codecrafters.inventorypro.utils.SessionManager
import com.codecrafters.inventorypro.utils.gone
import com.codecrafters.inventorypro.utils.showToast
import com.codecrafters.inventorypro.utils.visible
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private val userRepository = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val RC_GOOGLE_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupClickListeners()
    }

    private fun setupClickListeners() {

        // Email/Password login
        binding.btnLogin.setOnClickListener {
            val email    = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (validateInputs(email, password)) {
                loginWithEmail(email, password)
            }
        }

        // Forgot password
        binding.tvForgotPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                binding.tilEmail.error = "Enter your email address first"
                return@setOnClickListener
            }
            binding.tilEmail.error = null
            sendPasswordReset(email)
        }

        // Google Sign-In
        binding.btnGoogleSignIn.setOnClickListener {
            startGoogleSignIn()
        }

        // Navigate to Register
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var valid = true

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            valid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Enter a valid email"
            valid = false
        } else {
            binding.tilEmail.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            valid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            valid = false
        } else {
            binding.tilPassword.error = null
        }

        return valid
    }

    private fun loginWithEmail(email: String, password: String) {
        binding.progressBar.visible()
        binding.btnLogin.isEnabled = false

        lifecycleScope.launch {
            val result = userRepository.loginWithEmail(email, password)

            result.onSuccess { user ->
                sessionManager.saveSession(user)
                binding.progressBar.gone()
                navigateToDashboard()
            }

            result.onFailure { error ->
                binding.progressBar.gone()
                binding.btnLogin.isEnabled = true
                val message = when {
                    error.message?.contains("password") == true ->
                        "Incorrect password. Try again or use Forgot Password."
                    error.message?.contains("no user") == true ||
                            error.message?.contains("identifier") == true ->
                        "No account found with this email."
                    error.message?.contains("network") == true ->
                        "Network error. Check your connection."
                    else -> "Login failed: ${error.message}"
                }
                showToast(message)
            }
        }
    }

    private fun sendPasswordReset(email: String) {
        binding.progressBar.visible()
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                binding.progressBar.gone()
                if (task.isSuccessful) {
                    showToast("Password reset email sent to $email — check your inbox")
                } else {
                    showToast("Failed: ${task.exception?.message}")
                }
            }
    }

    private fun startGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.codecrafters.inventorypro.R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        startActivityForResult(googleSignInClient.signInIntent, RC_GOOGLE_SIGN_IN)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken ?: return
                binding.progressBar.visible()
                lifecycleScope.launch {
                    val result = userRepository.loginWithGoogle(idToken)
                    result.onSuccess { user ->
                        sessionManager.saveSession(user)
                        binding.progressBar.gone()
                        navigateToDashboard()
                    }
                    result.onFailure { error ->
                        binding.progressBar.gone()
                        showToast("Google Sign-In failed: ${error.message}")
                    }
                }
            } catch (e: ApiException) {
                showToast("Google Sign-In error: ${e.message}")
            }
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}