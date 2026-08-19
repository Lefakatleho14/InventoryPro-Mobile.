package com.codecrafters.inventorypro.models

data class Product(
    val id: String = "",
    val productName: String = "",
    val description: String = "",
    val quantityInStock: Int = 0,
    val unitPrice: Double = 0.0,
    val reorderLevel: Int = 5,
    val supplier: String = "",
    val lastUpdated: String = ""
) {
    // Computed properties
    val isLowStock: Boolean
        get() = quantityInStock <= reorderLevel

    val stockValue: Double
        get() = quantityInStock * unitPrice

    val formattedPrice: String
        get() = "R %.2f".format(unitPrice)

    val formattedStockValue: String
        get() = "R %.2f".format(stockValue)
}