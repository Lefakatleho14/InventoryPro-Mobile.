package com.codecrafters.inventorypro.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codecrafters.inventorypro.R
import com.codecrafters.inventorypro.activities.AddEditProductActivity
import com.codecrafters.inventorypro.adapters.ProductAdapter
import com.codecrafters.inventorypro.models.Product
import com.codecrafters.inventorypro.repository.ProductRepository
import com.codecrafters.inventorypro.utils.SessionManager
import com.codecrafters.inventorypro.utils.gone
import com.codecrafters.inventorypro.utils.showToast
import com.codecrafters.inventorypro.utils.visible
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import android.widget.Toast

class ProductsFragment : Fragment() {

    private val repository = ProductRepository()
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: ProductAdapter

    private var allProducts   = listOf<Product>()
    private var activeFilter  = "all"

    // Launcher for add/edit result
    private val addEditLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            loadProducts()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_products, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val isManager  = sessionManager.isManager()

        // ── FAB — visible to Manager only ─────────────────────────────────
        val fab = view.findViewById<FloatingActionButton>(R.id.fabAddProduct)
        if (isManager) {
            fab.visible()
            fab.setOnClickListener {
                addEditLauncher.launch(
                    Intent(requireContext(), AddEditProductActivity::class.java)
                )
            }
        }

        // ── RecyclerView ──────────────────────────────────────────────────
        val rv = view.findViewById<RecyclerView>(R.id.rvProducts)
        adapter = ProductAdapter(
            products     = emptyList(),
            onItemClick  = { product -> showProductDetail(product) },
            onEditClick  = { product -> editProduct(product) },
            onDeleteClick = { product -> confirmDelete(product) },
            isManager    = isManager
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        // ── Swipe to refresh ──────────────────────────────────────────────
        val swipe = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        swipe.setOnRefreshListener {
            loadProducts()
            swipe.isRefreshing = false
        }

        // ── Search ────────────────────────────────────────────────────────
        val etSearch = view.findViewById<TextInputEditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun afterTextChanged(s: Editable?) {
                applyFilters(s.toString())
            }
        })

        // ── Filter chips ──────────────────────────────────────────────────
        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroup)
        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            activeFilter = when {
                checkedIds.contains(R.id.chipLowStock) -> "low"
                checkedIds.contains(R.id.chipInStock)  -> "instock"
                else                                   -> "all"
            }
            applyFilters(etSearch.text.toString())
        }

        loadProducts()
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            val result = repository.getAllProducts()
            result.onSuccess { products ->
                allProducts = products.sortedBy { it.productName }
                applyFilters(
                    view?.findViewById<TextInputEditText>(R.id.etSearch)
                        ?.text.toString() ?: ""
                )
                view?.findViewById<TextView>(R.id.tvProductCount)?.text =
                    "${products.size} products · " +
                            "${products.count { it.isLowStock }} low stock"
            }
            result.onFailure {
                Toast.makeText(requireContext(), "Failed to load products", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun applyFilters(query: String) {
        var filtered = when (activeFilter) {
            "low"     -> allProducts.filter { it.isLowStock }
            "instock" -> allProducts.filter { !it.isLowStock }
            else      -> allProducts
        }
        if (query.isNotEmpty()) {
            filtered = filtered.filter {
                it.productName.contains(query, ignoreCase = true) ||
                        it.supplier.contains(query, ignoreCase = true)
            }
        }

        adapter.updateData(filtered)

        val emptyLayout = view?.findViewById<LinearLayout>(R.id.layoutEmpty)
        val rv          = view?.findViewById<RecyclerView>(R.id.rvProducts)
        if (filtered.isEmpty()) {
            emptyLayout?.visible()
            rv?.gone()
        } else {
            emptyLayout?.gone()
            rv?.visible()
        }
    }

    private fun showProductDetail(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle(product.productName)
            .setMessage(
                "ID: ${product.id}\n" +
                        "Description: ${product.description.ifEmpty { "N/A" }}\n" +
                        "Qty In Stock: ${product.quantityInStock}\n" +
                        "Unit Price: ${product.formattedPrice}\n" +
                        "Stock Value: ${product.formattedStockValue}\n" +
                        "Reorder Level: ${product.reorderLevel}\n" +
                        "Supplier: ${product.supplier.ifEmpty { "N/A" }}\n" +
                        "Status: ${if (product.isLowStock) "⚠️ LOW STOCK" else "✅ OK"}"
            )
            .setPositiveButton("Close", null)
            .apply {
                if (sessionManager.isManager()) {
                    setNeutralButton("Edit") { _, _ -> editProduct(product) }
                }
            }
            .show()
    }

    private fun editProduct(product: Product) {
        val intent = Intent(requireContext(), AddEditProductActivity::class.java)
        intent.putExtra(AddEditProductActivity.EXTRA_PRODUCT_ID, product.id)
        addEditLauncher.launch(intent)
    }

    private fun confirmDelete(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Product")
            .setMessage(
                "Are you sure you want to delete '${product.productName}'?\n\n" +
                        "This action cannot be undone."
            )
            .setPositiveButton("Delete") { _, _ -> deleteProduct(product) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteProduct(product: Product) {
        lifecycleScope.launch {
            val result = repository.deleteProduct(product.id)
            result.onSuccess {
                Toast.makeText(requireContext(), "'${product.productName}' deleted", Toast.LENGTH_SHORT).show()
                loadProducts()
            }
            result.onFailure {
                Toast.makeText(requireContext(), "Delete failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}