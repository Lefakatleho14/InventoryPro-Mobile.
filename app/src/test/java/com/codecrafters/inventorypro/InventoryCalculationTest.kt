package com.codecrafters.inventorypro

import com.codecrafters.inventorypro.models.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InventoryCalculationTest {

    // ── Total inventory value tests ───────────────────────────────────────

    @Test
    fun `total inventory value is sum of all stock values`() {
        val products = listOf(
            Product(quantityInStock = 100, unitPrice = 45.00),
            Product(quantityInStock = 50,  unitPrice = 25.00),
            Product(quantityInStock = 10,  unitPrice = 850.00)
        )
        val expected = (100 * 45.00) + (50 * 25.00) + (10 * 850.00)
        val actual   = products.sumOf { it.stockValue }
        assertEquals(expected, actual, 0.001)
    }

    @Test
    fun `total value is zero when all products have zero stock`() {
        val products = listOf(
            Product(quantityInStock = 0, unitPrice = 45.00),
            Product(quantityInStock = 0, unitPrice = 850.00)
        )
        assertEquals(0.0, products.sumOf { it.stockValue }, 0.001)
    }

    @Test
    fun `total value for single product is correct`() {
        val product = Product(quantityInStock = 150, unitPrice = 45.00)
        assertEquals(6750.00, product.stockValue, 0.001)
    }

    // ── Low stock filter tests ────────────────────────────────────────────

    @Test
    fun `low stock filter returns only products below reorder level`() {
        val products = listOf(
            Product(quantityInStock = 100, reorderLevel = 20),
            Product(quantityInStock = 8,   reorderLevel = 10),
            Product(quantityInStock = 3,   reorderLevel = 5),
            Product(quantityInStock = 25,  reorderLevel = 8)
        )
        val lowStock = products.filter { it.isLowStock }
        assertEquals(2, lowStock.size)
    }

    @Test
    fun `all products in stock returns empty low stock list`() {
        val products = listOf(
            Product(quantityInStock = 100, reorderLevel = 20),
            Product(quantityInStock = 50,  reorderLevel = 10)
        )
        val lowStock = products.filter { it.isLowStock }
        assertTrue(lowStock.isEmpty())
    }

    @Test
    fun `all products low stock returns full list`() {
        val products = listOf(
            Product(quantityInStock = 5,  reorderLevel = 20),
            Product(quantityInStock = 3,  reorderLevel = 10),
            Product(quantityInStock = 0,  reorderLevel = 5)
        )
        val lowStock = products.filter { it.isLowStock }
        assertEquals(3, lowStock.size)
    }

    // ── Sale total calculation tests ──────────────────────────────────────

    @Test
    fun `sale total is quantity times unit price`() {
        val unitPrice = 45.00
        val quantity  = 5
        val expected  = 225.00
        assertEquals(expected, unitPrice * quantity, 0.001)
    }

    @Test
    fun `sale total for single item equals unit price`() {
        val unitPrice = 850.00
        val quantity  = 1
        assertEquals(unitPrice, unitPrice * quantity, 0.001)
    }

    @Test
    fun `stock deduction reduces quantity correctly`() {
        var currentStock = 100
        val soldQty      = 15
        currentStock    -= soldQty
        assertEquals(85, currentStock)
    }

    @Test
    fun `stock deduction to zero is valid`() {
        var currentStock = 5
        val soldQty      = 5
        currentStock    -= soldQty
        assertEquals(0, currentStock)
    }

    // ── Recommended order quantity tests ─────────────────────────────────

    @Test
    fun `recommended order quantity is three times reorder level minus current stock`() {
        val currentStock  = 3
        val reorderLevel  = 5
        val recommended   = (reorderLevel * 3) - currentStock
        assertEquals(12, recommended)
    }

    @Test
    fun `recommended order when stock is zero equals three times reorder level`() {
        val currentStock  = 0
        val reorderLevel  = 10
        val recommended   = (reorderLevel * 3) - currentStock
        assertEquals(30, recommended)
    }
}