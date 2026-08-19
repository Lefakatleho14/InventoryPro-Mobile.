package com.codecrafters.inventorypro.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codecrafters.inventorypro.R
import com.codecrafters.inventorypro.models.Product

class ProductSearchAdapter(
    private var products: List<Product>,
    private val onProductSelected: (Product) -> Unit
) : RecyclerView.Adapter<ProductSearchAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView  = view.findViewById(R.id.tvSearchProductName)
        val tvStock: TextView = view.findViewById(R.id.tvSearchProductStock)
        val tvPrice: TextView = view.findViewById(R.id.tvSearchProductPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.tvName.text  = product.productName
        holder.tvStock.text = "Stock: ${product.quantityInStock}"
        holder.tvPrice.text = product.formattedPrice
        holder.itemView.setOnClickListener { onProductSelected(product) }
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}