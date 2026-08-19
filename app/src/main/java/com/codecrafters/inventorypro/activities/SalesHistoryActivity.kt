package com.codecrafters.inventorypro.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codecrafters.inventorypro.adapters.RecentSaleAdapter
import com.codecrafters.inventorypro.databinding.ActivitySalesHistoryBinding
import com.codecrafters.inventorypro.models.Sale
import com.codecrafters.inventorypro.repository.SaleRepository
import com.codecrafters.inventorypro.utils.gone
import com.codecrafters.inventorypro.utils.visible
import kotlinx.coroutines.launch

class SalesHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalesHistoryBinding
    private lateinit var adapter: RecentSaleAdapter
    private val saleRepository = SaleRepository()
    private var allSales = listOf<Sale>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Sales History"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
        setupSearch()
        setupSwipeRefresh()
        loadSales()
    }

    private fun setupRecyclerView() {
        adapter = RecentSaleAdapter(emptyList())
        binding.rvSales.layoutManager = LinearLayoutManager(this)
        binding.rvSales.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterSales(s.toString())
            }
        })
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadSales()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun loadSales() {
        lifecycleScope.launch {
            val result = saleRepository.getAllSales()
            result.onSuccess { sales ->
                allSales = sales
                filterSales(binding.etSearch.text.toString())

                // Summary
                val total = sales.sumOf { it.totalPrice }
                binding.tvTotalRevenue.text =
                    "R ${String.format("%.2f", total)}"
                binding.tvTotalTransactions.text = sales.size.toString()
            }
        }
    }

    private fun filterSales(query: String) {
        val filtered = if (query.isEmpty()) allSales
        else allSales.filter {
            it.productName.contains(query, ignoreCase = true) ||
                    it.servedBy.contains(query, ignoreCase = true)
        }

        adapter.updateData(filtered)

        if (filtered.isEmpty()) {
            binding.tvEmpty.visible()
            binding.rvSales.gone()
        } else {
            binding.tvEmpty.gone()
            binding.rvSales.visible()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}