package com.codecrafters.inventorypro.utils

object FirestoreHelper {
    const val COLLECTION_USERS    = "users"
    const val COLLECTION_PRODUCTS = "products"
    const val COLLECTION_SALES    = "sales"

    // User fields
    const val FIELD_FULL_NAME  = "fullName"
    const val FIELD_EMAIL      = "email"
    const val FIELD_ROLE       = "role"
    const val FIELD_CREATED_AT = "createdAt"

    // Product fields
    const val FIELD_PRODUCT_NAME      = "productName"
    const val FIELD_DESCRIPTION       = "description"
    const val FIELD_QUANTITY_IN_STOCK = "quantityInStock"
    const val FIELD_UNIT_PRICE        = "unitPrice"
    const val FIELD_REORDER_LEVEL     = "reorderLevel"
    const val FIELD_SUPPLIER          = "supplier"
    const val FIELD_LAST_UPDATED      = "lastUpdated"

    // Sale fields
    const val FIELD_PRODUCT_ID    = "productId"
    const val FIELD_PRODUCT_NAME2 = "productName"
    const val FIELD_QTY_SOLD      = "quantitySold"
    const val FIELD_TOTAL_PRICE   = "totalPrice"
    const val FIELD_SALE_DATE     = "saleDate"
    const val FIELD_SERVED_BY     = "servedBy"
    const val FIELD_USER_ROLE     = "userRole"
}