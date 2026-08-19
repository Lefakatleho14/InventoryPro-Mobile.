package com.codecrafters.inventorypro.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.codecrafters.inventorypro.R
import com.codecrafters.inventorypro.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sessionManager = SessionManager(this)

        // Wait 2 seconds then check auth state
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthAndNavigate()
        }, 2000)
    }

    private fun checkAuthAndNavigate() {
        val currentUser = auth.currentUser

        if (currentUser != null && sessionManager.isLoggedIn()) {
            // Already logged in — go to main screen
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // Not logged in — go to login screen
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}