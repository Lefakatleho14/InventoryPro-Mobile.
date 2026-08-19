package com.codecrafters.inventorypro.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.codecrafters.inventorypro.R
import com.codecrafters.inventorypro.activities.RecordSaleActivity

class SalesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        view.findViewById<TextView>(R.id.tvUserName).text = "Sales"
        view.findViewById<TextView>(R.id.tvUserRole).text =
            "Tap below to record a new sale"

        // Launch RecordSaleActivity immediately when Sales tab is tapped
        view.post {
            startActivity(Intent(requireContext(), RecordSaleActivity::class.java))
        }

        return view
    }
}