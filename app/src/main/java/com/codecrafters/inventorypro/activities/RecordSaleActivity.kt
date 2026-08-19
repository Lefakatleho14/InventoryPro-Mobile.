package com.codecrafters.inventorypro.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecrafters.inventorypro.adapters.ProductSearchAdapter
import com.codecrafters.inventorypro.databinding.ActivityRecordSaleBinding
import com.codecrafters.inventorypro.models.Product
import com.codecrafters.inventorypro.models.Sale
import com.codecrafters.inventorypro.repository.ProductRepository
import com.codecrafters.inventorypro.repository.SaleRepository
import com.codecrafters.inventorypro.utils.SessionManager
import com.codecrafters.inventorypro.utils.getCurrentDate
import com.codecrafters.inventorypro.utils.gone
import com.codecrafters.inventorypro.utils.visible
import kotlinx.coroutines.launch

class RecordSaleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordSaleBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var searchAdapter: ProductSearchAdapter

    private val productRepository = ProductRepository()
    private val saleRepository    = SaleRepository()

    private var allProducts      = listOf<Product>()
    private var selectedProduct: Product? = null
    private var quantity         = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordSaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        supportActionBar?.title = "Record Sale"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupSearchRecyclerView()
        setupClickListeners()
        loadProducts()
    }

    private fun setupSearchRecyclerView() {
        searchAdapter = ProductSearchAdapter(emptyList()) { product ->
            selectProduct(product)
        }
        binding.rvProductSearch.layoutManager = LinearLayoutManager(this)
        binding.rvProductSearch.adapter = searchAdapter
    }

    private fun setupClickListeners() {

        // Product search
        binding.etProductSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    binding.rvProductSearch.gone()
                    return
                }
                val filtered = allProducts.filter {
                    it.productName.contains(query, ignoreCase = true) &&
                            it.quantityInStock > 0
                }
                searchAdapter.updateData(filtered)
                binding.rvProductSearch.visibility =
                    if (filtered.isEmpty()) View.GONE else View.VISIBLE
            }
        })

        // Minus button
        binding.btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.etQty.setText(quantity.toString())
                updateTotal()
            }
        }

        // Plus button
        binding.btnPlus.setOnClickListener {
            val maxQty = selectedProduct?.quantityInStock ?: 1
            if (quantity < maxQty) {
                quantity++
                binding.etQty.setText(quantity.toString())
                updateTotal()
            } else {
                binding.tvStockWarning.text =
                    "Maximum available: $maxQty unit(s)"
                binding.tvStockWarning.visible()
            }
        }

        // Qty text change
        binding.etQty.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString().toIntOrNull() ?: 1
                quantity = input
                updateTotal()
                val maxQty = selectedProduct?.quantityInStock ?: 0
                if (input > maxQty && maxQty > 0) {
                    binding.tvStockWarning.text =
                        "Only $maxQty unit(s) available"
                    binding.tvStockWarning.visible()
                    binding.btnConfirmSale.isEnabled = false
                } else {
                    binding.tvStockWarning.gone()
                    binding.btnConfirmSale.isEnabled = true
                }
            }
        })

        // Confirm sale
        binding.btnConfirmSale.setOnClickListener {
            confirmSale()
        }

        // Cancel
        binding.btnCancelSale.setOnClickListener {
            finish()
        }
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            val result = productRepository.getAllProducts()
            result.onSuccess { products ->
                allProducts = products.filter { it.quantityInStock > 0 }
            }
        }
    }

    private fun selectProduct(product: Product) {
        selectedProduct = product
        quantity = 1

        // Hide search list
        binding.rvProductSearch.gone()
        binding.etProductSearch.setText(product.productName)
        binding.etProductSearch.clearFocus()

        // Show selected product card
        binding.cardSelectedProduct.visible()
        binding.tvSelectedProductName.text  = product.productName
        binding.tvSelectedProductPrice.text = "Price: ${product.formattedPrice}"
        binding.tvSelectedProductStock.text = "Stock: ${product.quantityInStock}"

        // Reset qty
        binding.etQty.setText("1")
        binding.tvStockWarning.gone()
        binding.btnConfirmSale.isEnabled = true

        updateTotal()
    }

    private fun updateTotal() {
        val product = selectedProduct ?: return
        val total   = product.unitPrice * quantity
        binding.tvTotalPrice.text = "R ${String.format("%.2f", total)}"
    }

    private fun confirmSale() {
        val product = selectedProduct
        if (product == null) {
            binding.tilProductSearch.error = "Please select a product"
            return
        }

        val qty = binding.etQty.text.toString().toIntOrNull() ?: 0
        if (qty <= 0) {
            binding.tilQty.error = "Quantity must be at least 1"
            return
        }
        if (qty > product.quantityInStock) {
            binding.tilQty.error = "Only ${product.quantityInStock} unit(s) available"
            return
        }

        binding.tilProductSearch.error = null
        binding.tilQty.error           = null
        binding.progressBar.visible()
        binding.btnConfirmSale.isEnabled = false

        val user       = sessionManager.getUser()
        val totalPrice = product.unitPrice * qty

        val sale = Sale(
            productId    = product.id,
            productName  = product.productName,
            quantitySold = qty,
            unitPrice    = product.unitPrice,
            totalPrice   = totalPrice,
            saleDate     = getCurrentDate(),
            servedBy     = user.fullName,
            userRole     = user.role
        )

        lifecycleScope.launch {
            val result = saleRepository.recordSale(sale)

            result.onSuccess { saleId ->
                binding.progressBar.gone()
                // Navigate to receipt
                val intent = Intent(this@RecordSaleActivity, ReceiptActivity::class.java)
                intent.putExtra(ReceiptActivity.EXTRA_SALE_ID,       saleId)
                intent.putExtra(ReceiptActivity.EXTRA_PRODUCT_NAME,  product.productName)
                intent.putExtra(ReceiptActivity.EXTRA_QTY,           qty)
                intent.putExtra(ReceiptActivity.EXTRA_UNIT_PRICE,    product.unitPrice)
                intent.putExtra(ReceiptActivity.EXTRA_TOTAL_PRICE,   totalPrice)
                intent.putExtra(ReceiptActivity.EXTRA_SERVED_BY,     user.fullName)
                intent.putExtra(ReceiptActivity.EXTRA_DATE,          getCurrentDate())
                startActivity(intent)
                finish()
            }

            result.onFailure { error ->
                binding.progressBar.gone()
                binding.btnConfirmSale.isEnabled = true
                val msg = error.message ?: "Sale failed"
                android.widget.Toast.makeText(
                    this@RecordSaleActivity, msg, android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}