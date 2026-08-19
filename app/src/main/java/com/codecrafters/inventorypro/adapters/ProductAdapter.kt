package com.codecrafters.inventorypro.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codecrafters.inventorypro.R
import com.codecrafters.inventorypro.models.Product

class ProductAdapter(
    private var products: List<Product>,
    private val onItemClick: (Product) -> Unit,
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit,
    private val isManager: Boolean
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView       = view.findViewById(R.id.tvProductName)
        val tvSupplier: TextView   = view.findViewById(R.id.tvSupplier)
        val tvQty: TextView        = view.findViewById(R.id.tvQty)
        val tvPrice: TextView      = view.findViewById(R.id.tvPrice)
        val tvStockValue: TextView = view.findViewById(R.id.tvStockValue)
        val tvLowStock: TextView   = view.findViewById(R.id.tvLowStockBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]

        holder.tvName.text       = product.productName
        holder.tvSupplier.text   = if (product.supplier.isNotEmpty())
            "Supplier: ${product.supplier}" else "No supplier"
        holder.tvQty.text        = product.quantityInStock.toString()
        holder.tvPrice.text      = product.formattedPrice
        holder.tvStockValue.text = "R${String.format("%.0f", product.stockValue)}"

        // Low stock badge
        if (product.isLowStock) {
            holder.tvLowStock.visibility = View.VISIBLE
            holder.tvQty.setTextColor(
                holder.itemView.context.getColor(R.color.danger))
        } else {
            holder.tvLowStock.visibility = View.GONE
            holder.tvQty.setTextColor(
                holder.itemView.context.getColor(R.color.primary))
        }

        // Click listeners
        holder.itemView.setOnClickListener { onItemClick(product) }

        holder.itemView.setOnLongClickListener {
            if (isManager) {
                showContextMenu(holder.itemView, product)
            }
            true
        }
    }

    private fun showContextMenu(view: View, product: Product) {
        val popup = android.widget.PopupMenu(view.context, view)
        popup.menu.add(0, 1, 0, "✏️ Edit")
        popup.menu.add(0, 2, 1, "🗑️ Delete")
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> onEditClick(product)
                2 -> onDeleteClick(product)
            }
            true
        }
        popup.show()
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    fun filter(query: String, original: List<Product>): List<Product> {
        return if (query.isEmpty()) original
        else original.filter {
            it.productName.contains(query, ignoreCase = true) ||
                    it.supplier.contains(query, ignoreCase = true)
        }
    }
}