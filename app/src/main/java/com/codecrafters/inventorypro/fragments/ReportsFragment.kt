package com.codecrafters.inventorypro.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.codecrafters.inventorypro.R
import com.codecrafters.inventorypro.activities.ReportsActivity
import com.codecrafters.inventorypro.utils.SessionManager

class ReportsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        view.findViewById<TextView>(R.id.tvUserName).text = "Reports"

        val session = SessionManager(requireContext())
        if (session.isManager()) {
            view.post {
                startActivity(Intent(requireContext(), ReportsActivity::class.java))
            }
        } else {
            view.findViewById<TextView>(R.id.tvUserRole).text =
                "Reports are only available to Managers"
        }
        return view
    }
}