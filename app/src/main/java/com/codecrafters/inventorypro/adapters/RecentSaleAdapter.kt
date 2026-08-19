package com.codecrafters.inventorypro.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codecrafters.inventorypro.R
import com.codecrafters.inventorypro.models.Sale

class RecentSaleAdapter(
    private var sales: List<Sale>
) : RecyclerView.Adapter<RecentSaleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProduct: TextView  = view.findViewById(R.id.tvSaleProduct)
        val tvDate: TextView     = view.findViewById(R.id.tvSaleDate)
        val tvServedBy: TextView = view.findViewById(R.id.tvSaleServedBy)
        val tvTotal: TextView    = view.findViewById(R.id.tvSaleTotal)
        val tvQty: TextView      = view.findViewById(R.id.tvSaleQty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_sale, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sale = sales[position]
        holder.tvProduct.text  = sale.productName
        holder.tvDate.text     = sale.saleDate
        holder.tvServedBy.text = "By: ${sale.servedBy}"
        holder.tvTotal.text    = sale.formattedTotal
        holder.tvQty.text      = "Qty: ${sale.quantitySold}"
    }

    override fun getItemCount(): Int = sales.size

    fun updateData(newSales: List<Sale>) {
        sales = newSales
        notifyDataSetChanged()
    }
}