package com.codecrafters.inventorypro.models

data class Sale(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val quantitySold: Int = 0,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0,
    val saleDate: String = "",
    val servedBy: String = "",
    val userRole: String = ""
) {
    val formattedTotal: String
        get() = "R %.2f".format(totalPrice)

    val formattedUnitPrice: String
        get() = "R %.2f".format(unitPrice)
}