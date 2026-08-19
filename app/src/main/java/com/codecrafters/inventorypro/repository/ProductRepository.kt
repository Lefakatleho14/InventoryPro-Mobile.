package com.codecrafters.inventorypro.repository

import com.codecrafters.inventorypro.models.Product
import com.codecrafters.inventorypro.utils.FirestoreHelper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection(FirestoreHelper.COLLECTION_PRODUCTS)

    // ── Get all products ──────────────────────────────────────────────────
    suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            val snapshot = collection.get().await()
            val products = snapshot.documents.map { doc ->
                Product(
                    id              = doc.id,
                    productName     = doc.getString(FirestoreHelper.FIELD_PRODUCT_NAME) ?: "",
                    description     = doc.getString(FirestoreHelper.FIELD_DESCRIPTION) ?: "",
                    quantityInStock = (doc.getLong(FirestoreHelper.FIELD_QUANTITY_IN_STOCK) ?: 0).toInt(),
                    unitPrice       = doc.getDouble(FirestoreHelper.FIELD_UNIT_PRICE) ?: 0.0,
                    reorderLevel    = (doc.getLong(FirestoreHelper.FIELD_REORDER_LEVEL) ?: 5).toInt(),
                    supplier        = doc.getString(FirestoreHelper.FIELD_SUPPLIER) ?: "",
                    lastUpdated     = doc.getString(FirestoreHelper.FIELD_LAST_UPDATED) ?: ""
                )
            }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Get low stock products ────────────────────────────────────────────
    suspend fun getLowStockProducts(): Result<List<Product>> {
        return try {
            val result = getAllProducts()
            if (result.isSuccess) {
                val lowStock = result.getOrNull()
                    ?.filter { it.isLowStock }
                    ?.sortedBy { it.quantityInStock }
                    ?: emptyList()
                Result.success(lowStock)
            } else {
                result
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Add product ───────────────────────────────────────────────────────
    suspend fun addProduct(product: Product): Result<String> {
        return try {
            val doc = collection.add(productToMap(product)).await()
            Result.success(doc.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Update product ────────────────────────────────────────────────────
    suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            collection.document(product.id)
                .set(productToMap(product))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Delete product ────────────────────────────────────────────────────
    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            collection.document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Deduct stock after sale ───────────────────────────────────────────
    suspend fun deductStock(productId: String, quantity: Int): Result<Unit> {
        return try {
            val doc     = collection.document(productId).get().await()
            val current = (doc.getLong(FirestoreHelper.FIELD_QUANTITY_IN_STOCK) ?: 0).toInt()
            val newQty  = current - quantity

            if (newQty < 0) {
                return Result.failure(Exception("Insufficient stock"))
            }

            collection.document(productId)
                .update(FirestoreHelper.FIELD_QUANTITY_IN_STOCK, newQty)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Get total inventory value ─────────────────────────────────────────
    suspend fun getTotalInventoryValue(): Result<Double> {
        return try {
            val result = getAllProducts()
            if (result.isSuccess) {
                val total = result.getOrNull()?.sumOf { it.stockValue } ?: 0.0
                Result.success(total)
            } else {
                Result.failure(Exception("Failed to calculate value"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Helper: convert Product to Firestore map ──────────────────────────
    private fun productToMap(product: Product): Map<String, Any> = mapOf(
        FirestoreHelper.FIELD_PRODUCT_NAME      to product.productName,
        FirestoreHelper.FIELD_DESCRIPTION       to product.description,
        FirestoreHelper.FIELD_QUANTITY_IN_STOCK to product.quantityInStock,
        FirestoreHelper.FIELD_UNIT_PRICE        to product.unitPrice,
        FirestoreHelper.FIELD_REORDER_LEVEL     to product.reorderLevel,
        FirestoreHelper.FIELD_SUPPLIER          to product.supplier,
        FirestoreHelper.FIELD_LAST_UPDATED      to getCurrentDate()
    )

    private fun getCurrentDate(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}