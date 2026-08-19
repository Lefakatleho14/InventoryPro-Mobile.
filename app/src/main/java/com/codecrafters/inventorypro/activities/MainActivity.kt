package com.codecrafters.inventorypro.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.codecrafters.inventorypro.R
import com.codecrafters.inventorypro.fragments.DashboardFragment
import com.codecrafters.inventorypro.fragments.ProductsFragment
import com.codecrafters.inventorypro.fragments.ReportsFragment
import com.codecrafters.inventorypro.fragments.SalesFragment
import com.codecrafters.inventorypro.fragments.SettingsFragment
import com.codecrafters.inventorypro.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)

        bottomNav = findViewById(R.id.bottomNavigation)

        // Load dashboard on start
        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
        }

        // Hide Reports tab for Shop Assistants
        if (!sessionManager.isManager()) {
            bottomNav.menu.findItem(R.id.nav_reports).isVisible = false
        }

        // Bottom nav item selection
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> loadFragment(DashboardFragment())
                R.id.nav_products  -> loadFragment(ProductsFragment())
                R.id.nav_sales     -> loadFragment(SalesFragment())
                R.id.nav_reports   -> loadFragment(ReportsFragment())
                R.id.nav_settings  -> loadFragment(SettingsFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // Called from DashboardFragment quick action
    fun navigateToProducts() {
        bottomNav.selectedItemId = R.id.nav_products
    }
}