package com.codecrafters.inventorypro.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codecrafters.inventorypro.R
import com.codecrafters.inventorypro.models.Product

class LowStockAdapter(
    private var products: List<Product>
) : RecyclerView.Adapter<LowStockAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView     = view.findViewById(R.id.tvLowStockName)
        val tvSupplier: TextView = view.findViewById(R.id.tvLowStockSupplier)
        val tvQty: TextView      = view.findViewById(R.id.tvLowStockQty)
        val tvReorder: TextView  = view.findViewById(R.id.tvLowStockReorder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_low_stock, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = products[position]
        holder.tvName.text     = p.productName
        holder.tvSupplier.text = p.supplier.ifEmpty { "No supplier" }
        holder.tvQty.text      = "${p.quantityInStock} left"
        holder.tvReorder.text  = "Reorder at: ${p.reorderLevel}"
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}