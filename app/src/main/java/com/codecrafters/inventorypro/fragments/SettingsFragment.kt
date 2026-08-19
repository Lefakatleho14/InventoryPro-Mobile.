package com.codecrafters.inventorypro.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.codecrafters.inventorypro.R
import com.codecrafters.inventorypro.activities.ChangePasswordActivity
import com.codecrafters.inventorypro.activities.LoginActivity
import com.codecrafters.inventorypro.activities.ReportsActivity
import com.codecrafters.inventorypro.activities.SalesHistoryActivity
import com.codecrafters.inventorypro.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val user = sessionManager.getUser()

        // ── Profile ───────────────────────────────────────────────────────
        view.findViewById<TextView>(R.id.tvAvatarInitial).text =
            user.fullName.firstOrNull()?.uppercase() ?: "U"
        view.findViewById<TextView>(R.id.tvProfileName).text  = user.fullName
        view.findViewById<TextView>(R.id.tvProfileEmail).text = user.email
        view.findViewById<TextView>(R.id.tvProfileRole).text  = user.role

        // ── Menu options ──────────────────────────────────────────────────
        view.findViewById<View>(R.id.layoutChangePassword).setOnClickListener {
            startActivity(Intent(requireContext(), ChangePasswordActivity::class.java))
        }

        view.findViewById<View>(R.id.layoutSalesHistory).setOnClickListener {
            startActivity(Intent(requireContext(), SalesHistoryActivity::class.java))
        }

        view.findViewById<View>(R.id.layoutAbout).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("About InventoryPro")
                .setMessage(
                    "InventoryPro v1.0\n\n" +
                            "Built by Team CodeCrafters\n" +
                            "Durban Small Business Supplies\n\n" +
                            "Final Year WIL Project\n" +
                            "Powered by Firebase + Kotlin"
                )
                .setPositiveButton("OK", null)
                .show()
        }

        // ── Reports — Manager only ────────────────────────────────────────
        if (user.isManager()) {
            view.findViewById<View>(R.id.layoutSalesHistory)
                .setOnClickListener {
                    startActivity(Intent(requireContext(), ReportsActivity::class.java))
                }
        }

        // ── Logout ────────────────────────────────────────────────────────
        view.findViewById<View>(R.id.btnLogout).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    sessionManager.clearSession()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}