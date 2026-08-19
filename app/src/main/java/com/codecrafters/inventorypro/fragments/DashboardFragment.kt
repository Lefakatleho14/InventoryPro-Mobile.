package com.codecrafters.inventorypro.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codecrafters.inventorypro.R
import com.codecrafters.inventorypro.activities.MainActivity
import com.codecrafters.inventorypro.adapters.RecentSaleAdapter
import com.codecrafters.inventorypro.repository.ProductRepository
import com.codecrafters.inventorypro.repository.SaleRepository
import com.codecrafters.inventorypro.utils.SessionManager
import com.codecrafters.inventorypro.utils.gone
import com.codecrafters.inventorypro.utils.visible
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.codecrafters.inventorypro.activities.RecordSaleActivity
import android.content.Intent

class DashboardFragment : Fragment() {

    private val productRepository = ProductRepository()
    private val saleRepository    = SaleRepository()
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: RecentSaleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val user = sessionManager.getUser()

        // ── Welcome text ──────────────────────────────────────────────────
        view.findViewById<TextView>(R.id.tvUserName).text = user.fullName
        view.findViewById<TextView>(R.id.tvUserRole).text = user.role
        view.findViewById<TextView>(R.id.tvDate).text =
            SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date())

        // ── RecyclerView ──────────────────────────────────────────────────
        val rvSales = view.findViewById<RecyclerView>(R.id.rvRecentSales)
        adapter = RecentSaleAdapter(emptyList())
        rvSales.layoutManager = LinearLayoutManager(requireContext())
        rvSales.adapter = adapter

        // ── Quick action buttons ──────────────────────────────────────────
        view.findViewById<View>(R.id.cardRecordSale).setOnClickListener {
            startActivity(Intent(requireContext(), RecordSaleActivity::class.java))
        }

        view.findViewById<View>(R.id.cardViewProducts).setOnClickListener {
            (activity as? MainActivity)?.navigateToProducts()
        }

        view.findViewById<View>(R.id.cardViewReports).setOnClickListener {
            if (user.isManager()) {
                Toast.makeText(requireContext(),
                    "Reports — coming soon!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(),
                    "Reports are only available to Managers",
                    Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<View>(R.id.tvViewLowStock).setOnClickListener {
            Toast.makeText(requireContext(),
                "Low Stock Report — coming soon!", Toast.LENGTH_SHORT).show()
        }

        // ── Load data ─────────────────────────────────────────────────────
        loadDashboardData(view)
    }

    override fun onResume() {
        super.onResume()
        view?.let { loadDashboardData(it) }
    }

    private fun loadDashboardData(view: View) {
        lifecycleScope.launch {
            try {
                // Products stats
                val productsResult = productRepository.getAllProducts()
                productsResult.onSuccess { products ->
                    val totalValue    = products.sumOf { it.stockValue }
                    val lowStockItems = products.filter { it.isLowStock }

                    view.findViewById<TextView>(R.id.tvTotalProducts).text =
                        products.size.toString()
                    view.findViewById<TextView>(R.id.tvInventoryValue).text =
                        "R ${String.format("%.0f", totalValue)}"
                    view.findViewById<TextView>(R.id.tvLowStockCount).text =
                        lowStockItems.size.toString()

                    val alertCard = view.findViewById<View>(R.id.cardLowStockAlert)
                    if (lowStockItems.isNotEmpty()) {
                        alertCard.visible()
                        view.findViewById<TextView>(R.id.tvLowStockAlert).text =
                            "⚠️ ${lowStockItems.size} product(s) need restocking"
                    } else {
                        alertCard.gone()
                    }
                }

                // Today's sales
                val todaySalesResult = saleRepository.getTodaysSalesTotal()
                todaySalesResult.onSuccess { total ->
                    view.findViewById<TextView>(R.id.tvTodaySales).text =
                        "R ${String.format("%.2f", total)}"
                }

                // Recent sales
                val salesResult = saleRepository.getAllSales()
                salesResult.onSuccess { sales ->
                    val recent = sales.take(5)
                    if (recent.isEmpty()) {
                        view.findViewById<TextView>(R.id.tvNoSales).visible()
                        view.findViewById<RecyclerView>(R.id.rvRecentSales).gone()
                    } else {
                        view.findViewById<TextView>(R.id.tvNoSales).gone()
                        view.findViewById<RecyclerView>(R.id.rvRecentSales).visible()
                        adapter.updateData(recent)
                    }
                }

            } catch (e: Exception) {
                // Data shows "--" placeholders until loaded
            }
        }
    }
}