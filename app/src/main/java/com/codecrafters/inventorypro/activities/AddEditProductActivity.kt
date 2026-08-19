package com.codecrafters.inventorypro.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.codecrafters.inventorypro.databinding.ActivityAddEditProductBinding
import com.codecrafters.inventorypro.models.Product
import com.codecrafters.inventorypro.repository.ProductRepository
import com.codecrafters.inventorypro.utils.gone
import com.codecrafters.inventorypro.utils.showToast
import com.codecrafters.inventorypro.utils.visible
import kotlinx.coroutines.launch

class AddEditProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditProductBinding
    private val repository = ProductRepository()

    private var productId: String = ""
    private var isEditMode = false

    companion object {
        const val EXTRA_PRODUCT_ID   = "product_id"
        const val EXTRA_PRODUCT_NAME = "product_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if editing existing product
        productId = intent.getStringExtra(EXTRA_PRODUCT_ID) ?: ""
        isEditMode = productId.isNotEmpty()

        supportActionBar?.title = if (isEditMode) "Edit Product" else "Add Product"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (isEditMode) loadProductData()

        binding.btnSave.setOnClickListener { saveProduct() }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun loadProductData() {
        lifecycleScope.launch {
            val result = repository.getAllProducts()
            result.onSuccess { products ->
                val product = products.find { it.id == productId } ?: return@onSuccess
                binding.etProductName.setText(product.productName)
                binding.etDescription.setText(product.description)
                binding.etUnitPrice.setText(product.unitPrice.toString())
                binding.etQty.setText(product.quantityInStock.toString())
                binding.etReorderLevel.setText(product.reorderLevel.toString())
                binding.etSupplier.setText(product.supplier)
            }
        }
    }

    private fun saveProduct() {
        val name         = binding.etProductName.text.toString().trim()
        val description  = binding.etDescription.text.toString().trim()
        val priceStr     = binding.etUnitPrice.text.toString().trim()
        val qtyStr       = binding.etQty.text.toString().trim()
        val reorderStr   = binding.etReorderLevel.text.toString().trim()
        val supplier     = binding.etSupplier.text.toString().trim()

        // Validation
        if (name.isEmpty()) {
            binding.tilProductName.error = "Product name is required"
            return
        }
        if (priceStr.isEmpty() || priceStr.toDoubleOrNull() == null
            || priceStr.toDouble() <= 0) {
            binding.tilUnitPrice.error = "Enter a valid price greater than 0"
            return
        }
        if (qtyStr.isEmpty() || qtyStr.toIntOrNull() == null
            || qtyStr.toInt() < 0) {
            binding.tilQty.error = "Enter a valid quantity (0 or more)"
            return
        }
        if (reorderStr.isEmpty() || reorderStr.toIntOrNull() == null) {
            binding.tilReorderLevel.error = "Enter a valid reorder level"
            return
        }

        // Clear errors
        binding.tilProductName.error  = null
        binding.tilUnitPrice.error    = null
        binding.tilQty.error          = null
        binding.tilReorderLevel.error = null

        val product = Product(
            id              = productId,
            productName     = name,
            description     = description,
            unitPrice       = priceStr.toDouble(),
            quantityInStock = qtyStr.toInt(),
            reorderLevel    = reorderStr.toInt(),
            supplier        = supplier
        )

        binding.btnSave.isEnabled = false

        lifecycleScope.launch {
            val result = if (isEditMode)
                repository.updateProduct(product)
            else
                repository.addProduct(product)

            result.onSuccess {
                showToast(if (isEditMode)
                    "Product updated successfully!"
                else
                    "Product added successfully!")
                setResult(RESULT_OK)
                finish()
            }

            result.onFailure { error ->
                binding.btnSave.isEnabled = true
                showToast("Error: ${error.message}")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}