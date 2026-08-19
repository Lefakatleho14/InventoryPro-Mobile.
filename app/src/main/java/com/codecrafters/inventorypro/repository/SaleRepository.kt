package com.codecrafters.inventorypro.repository

import com.codecrafters.inventorypro.models.Sale
import com.codecrafters.inventorypro.utils.FirestoreHelper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class SaleRepository {

    private val db         = FirebaseFirestore.getInstance()
    private val collection = db.collection(FirestoreHelper.COLLECTION_SALES)
    private val products   = ProductRepository()

    // ── Get all sales ─────────────────────────────────────────────────────
    suspend fun getAllSales(): Result<List<Sale>> {
        return try {
            val snapshot = collection
                .orderBy(FirestoreHelper.FIELD_SALE_DATE, Query.Direction.DESCENDING)
                .get()
                .await()

            val sales = snapshot.documents.map { doc ->
                Sale(
                    id           = doc.id,
                    productId    = doc.getString(FirestoreHelper.FIELD_PRODUCT_ID) ?: "",
                    productName  = doc.getString(FirestoreHelper.FIELD_PRODUCT_NAME2) ?: "",
                    quantitySold = (doc.getLong(FirestoreHelper.FIELD_QTY_SOLD) ?: 0).toInt(),
                    unitPrice    = doc.getDouble(FirestoreHelper.FIELD_UNIT_PRICE) ?: 0.0,
                    totalPrice   = doc.getDouble(FirestoreHelper.FIELD_TOTAL_PRICE) ?: 0.0,
                    saleDate     = doc.getString(FirestoreHelper.FIELD_SALE_DATE) ?: "",
                    servedBy     = doc.getString(FirestoreHelper.FIELD_SERVED_BY) ?: "",
                    userRole     = doc.getString(FirestoreHelper.FIELD_USER_ROLE) ?: ""
                )
            }
            Result.success(sales)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Record a sale and deduct stock ────────────────────────────────────
    suspend fun recordSale(sale: Sale): Result<String> {
        return try {
            // Deduct stock first
            val deductResult = products.deductStock(sale.productId, sale.quantitySold)
            if (deductResult.isFailure) {
                return Result.failure(
                    deductResult.exceptionOrNull() ?: Exception("Stock deduction failed")
                )
            }

            // Add sale record
            val saleMap = mapOf(
                FirestoreHelper.FIELD_PRODUCT_ID    to sale.productId,
                FirestoreHelper.FIELD_PRODUCT_NAME2 to sale.productName,
                FirestoreHelper.FIELD_QTY_SOLD      to sale.quantitySold,
                FirestoreHelper.FIELD_UNIT_PRICE    to sale.unitPrice,
                FirestoreHelper.FIELD_TOTAL_PRICE   to sale.totalPrice,
                FirestoreHelper.FIELD_SALE_DATE     to sale.saleDate,
                FirestoreHelper.FIELD_SERVED_BY     to sale.servedBy,
                FirestoreHelper.FIELD_USER_ROLE     to sale.userRole
            )

            val doc = collection.add(saleMap).await()
            Result.success(doc.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Get today's sales total ───────────────────────────────────────────
    suspend fun getTodaysSalesTotal(): Result<Double> {
        return try {
            val today    = getCurrentDate()
            val snapshot = collection
                .whereEqualTo(FirestoreHelper.FIELD_SALE_DATE, today)
                .get()
                .await()

            val total = snapshot.documents.sumOf {
                it.getDouble(FirestoreHelper.FIELD_TOTAL_PRICE) ?: 0.0
            }
            Result.success(total)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Get today's transaction count ─────────────────────────────────────
    suspend fun getTodaysTransactionCount(): Result<Int> {
        return try {
            val today    = getCurrentDate()
            val snapshot = collection
                .whereEqualTo(FirestoreHelper.FIELD_SALE_DATE, today)
                .get()
                .await()
            Result.success(snapshot.size())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getCurrentDate(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}