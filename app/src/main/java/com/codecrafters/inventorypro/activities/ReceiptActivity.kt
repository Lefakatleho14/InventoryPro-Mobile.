package com.codecrafters.inventorypro.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codecrafters.inventorypro.databinding.ActivityReceiptBinding

class ReceiptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptBinding

    companion object {
        const val EXTRA_SALE_ID      = "sale_id"
        const val EXTRA_PRODUCT_NAME = "product_name"
        const val EXTRA_QTY          = "qty"
        const val EXTRA_UNIT_PRICE   = "unit_price"
        const val EXTRA_TOTAL_PRICE  = "total_price"
        const val EXTRA_SERVED_BY    = "served_by"
        const val EXTRA_DATE         = "date"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Receipt"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get sale data from intent
        val saleId      = intent.getStringExtra(EXTRA_SALE_ID) ?: ""
        val productName = intent.getStringExtra(EXTRA_PRODUCT_NAME) ?: ""
        val qty         = intent.getIntExtra(EXTRA_QTY, 0)
        val unitPrice   = intent.getDoubleExtra(EXTRA_UNIT_PRICE, 0.0)
        val totalPrice  = intent.getDoubleExtra(EXTRA_TOTAL_PRICE, 0.0)
        val servedBy    = intent.getStringExtra(EXTRA_SERVED_BY) ?: ""
        val date        = intent.getStringExtra(EXTRA_DATE) ?: ""

        // Populate receipt
        binding.tvReceiptId.text        = "Receipt #${saleId.take(8).uppercase()}"
        binding.tvReceiptDate.text      = date
        binding.tvReceiptServedBy.text  = servedBy
        binding.tvReceiptProduct.text   = productName
        binding.tvReceiptQty.text       = "$qty unit(s)"
        binding.tvReceiptUnitPrice.text = "R ${String.format("%.2f", unitPrice)}"
        binding.tvReceiptTotal.text     = "R ${String.format("%.2f", totalPrice)}"

        // Buttons
        binding.btnNewSale.setOnClickListener {
            startActivity(Intent(this, RecordSaleActivity::class.java))
            finish()
        }

        binding.btnShareReceipt.setOnClickListener {
            shareReceipt(productName, qty, unitPrice, totalPrice, servedBy, date, saleId)
        }

        binding.btnGoHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun shareReceipt(
        productName: String,
        qty: Int,
        unitPrice: Double,
        totalPrice: Double,
        servedBy: String,
        date: String,
        saleId: String
    ) {
        val receiptText = """
            InventoryPro - Sale Receipt
            ===========================
            Receipt ID: ${saleId.take(8).uppercase()}
            Date:       $date
            Served by:  $servedBy
            
            Product:    $productName
            Quantity:   $qty unit(s)
            Unit Price: R ${String.format("%.2f", unitPrice)}
            ---------------------------
            TOTAL PAID: R ${String.format("%.2f", totalPrice)}
            ===========================
            Thank you for your purchase!
            Durban Small Business Supplies
        """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Sale Receipt - InventoryPro")
            putExtra(Intent.EXTRA_TEXT, receiptText)
        }
        startActivity(Intent.createChooser(shareIntent, "Share Receipt"))
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}