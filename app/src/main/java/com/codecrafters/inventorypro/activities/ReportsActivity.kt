package com.codecrafters.inventorypro.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecrafters.inventorypro.adapters.LowStockAdapter
import com.codecrafters.inventorypro.databinding.ActivityReportsBinding
import com.codecrafters.inventorypro.repository.ProductRepository
import com.codecrafters.inventorypro.repository.SaleRepository
import com.codecrafters.inventorypro.utils.gone
import com.codecrafters.inventorypro.utils.visible
import kotlinx.coroutines.launch

class ReportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportsBinding
    private lateinit var lowStockAdapter: LowStockAdapter
    private val productRepository = ProductRepository()
    private val saleRepository    = SaleRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Reports"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
        loadReportData()

        binding.btnExportReport.setOnClickListener { exportReport() }
        binding.btnViewSalesHistory.setOnClickListener {
            startActivity(Intent(this, SalesHistoryActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        lowStockAdapter = LowStockAdapter(emptyList())
        binding.rvLowStock.layoutManager = LinearLayoutManager(this)
        binding.rvLowStock.adapter = lowStockAdapter
        binding.rvLowStock.isNestedScrollingEnabled = false
    }

    private fun loadReportData() {
        lifecycleScope.launch {
            // Sales summary
            val salesResult = saleRepository.getAllSales()
            salesResult.onSuccess { sales ->
                val totalRevenue = sales.sumOf { it.totalPrice }
                binding.tvReportRevenue.text =
                    "R ${String.format("%.2f", totalRevenue)}"
                binding.tvReportTransactions.text = sales.size.toString()
            }

            // Inventory valuation + low stock
            val productsResult = productRepository.getAllProducts()
            productsResult.onSuccess { products ->
                val totalValue    = products.sumOf { it.stockValue }
                val lowStockItems = products.filter { it.isLowStock }
                    .sortedBy { it.quantityInStock }

                binding.tvValTotalProducts.text =
                    products.size.toString()
                binding.tvValTotalValue.text =
                    "R ${String.format("%.2f", totalValue)}"
                binding.tvValLowStock.text =
                    "${lowStockItems.size} item(s)"

                if (lowStockItems.isEmpty()) {
                    binding.tvNoLowStock.visible()
                    binding.rvLowStock.gone()
                } else {
                    binding.tvNoLowStock.gone()
                    binding.rvLowStock.visible()
                    lowStockAdapter.updateData(lowStockItems)
                }
            }
        }
    }

    private fun exportReport() {
        lifecycleScope.launch {
            val salesResult    = saleRepository.getAllSales()
            val productsResult = productRepository.getAllProducts()

            var reportText = """
                InventoryPro — Management Report
                =================================
                Generated: ${java.util.Date()}
                
            """.trimIndent()

            salesResult.onSuccess { sales ->
                val totalRevenue = sales.sumOf { it.totalPrice }
                reportText += """
                    
                    SALES SUMMARY
                    -------------
                    Total Transactions: ${sales.size}
                    Total Revenue: R ${String.format("%.2f", totalRevenue)}
                    
                """.trimIndent()
            }

            productsResult.onSuccess { products ->
                val totalValue    = products.sumOf { it.stockValue }
                val lowStockItems = products.filter { it.isLowStock }
                reportText += """
                    
                    INVENTORY SUMMARY
                    -----------------
                    Total Products: ${products.size}
                    Total Stock Value: R ${String.format("%.2f", totalValue)}
                    Low Stock Items: ${lowStockItems.size}
                    
                """.trimIndent()

                if (lowStockItems.isNotEmpty()) {
                    reportText += "\nLOW STOCK ITEMS\n---------------\n"
                    lowStockItems.forEach { p ->
                        reportText += "${p.productName}: ${p.quantityInStock} left " +
                                "(reorder at ${p.reorderLevel})\n"
                    }
                }
            }

            reportText += "\n=================================\n" +
                    "Durban Small Business Supplies\nInventoryPro — CodeCrafters"

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "InventoryPro Management Report")
                putExtra(Intent.EXTRA_TEXT, reportText)
            }
            startActivity(Intent.createChooser(shareIntent, "Share Report"))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}