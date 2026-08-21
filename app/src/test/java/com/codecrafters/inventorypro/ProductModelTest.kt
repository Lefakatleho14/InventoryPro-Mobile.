package com.codecrafters.inventorypro

import com.codecrafters.inventorypro.models.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProductModelTest {

    private lateinit var normalProduct: Product
    private lateinit var lowStockProduct: Product
    private lateinit var zeroStockProduct: Product

    @Before
    fun setup() {
        normalProduct = Product(
            id              = "test-001",
            productName     = "A4 Paper",
            description     = "White A4 paper",
            quantityInStock = 100,
            unitPrice       = 45.00,
            reorderLevel    = 20,
            supplier        = "PaperWorld"
        )

        lowStockProduct = Product(
            id              = "test-002",
            productName     = "Pens",
            description     = "Blue pens",
            quantityInStock = 8,
            unitPrice       = 25.00,
            reorderLevel    = 10,
            supplier        = "Stationery Hub"
        )

        zeroStockProduct = Product(
            id              = "test-003",
            productName     = "Filing Cabinet",
            description     = "Steel cabinet",
            quantityInStock = 0,
            unitPrice       = 850.00,
            reorderLevel    = 5,
            supplier        = "Office Essentials"
        )
    }

    // ── isLowStock tests ──────────────────────────────────────────────────

    @Test
    fun `product with stock above reorder level is not low stock`() {
        assertFalse(normalProduct.isLowStock)
    }

    @Test
    fun `product with stock below reorder level is low stock`() {
        assertTrue(lowStockProduct.isLowStock)
    }

    @Test
    fun `product with stock equal to reorder level is low stock`() {
        val product = normalProduct.copy(quantityInStock = 20)
        assertTrue(product.isLowStock)
    }

    @Test
    fun `product with zero stock is low stock`() {
        assertTrue(zeroStockProduct.isLowStock)
    }

    // ── stockValue tests ──────────────────────────────────────────────────

    @Test
    fun `stock value is calculated correctly`() {
        val expected = 100 * 45.00
        assertEquals(expected, normalProduct.stockValue, 0.001)
    }

    @Test
    fun `stock value is zero when quantity is zero`() {
        assertEquals(0.0, zeroStockProduct.stockValue, 0.001)
    }

    @Test
    fun `stock value for low stock product is correct`() {
        val expected = 8 * 25.00
        assertEquals(expected, lowStockProduct.stockValue, 0.001)
    }

    // ── formattedPrice tests ──────────────────────────────────────────────

    @Test
    fun `formatted price includes R prefix`() {
        assertTrue(normalProduct.formattedPrice.startsWith("R"))
    }

    @Test
    fun `formatted price shows correct amount`() {
        assertEquals("R 45.00", normalProduct.formattedPrice)
    }

    @Test
    fun `formatted stock value shows correct amount`() {
        val expected = "R 4500.00"
        assertEquals(expected, normalProduct.formattedStockValue)
    }

    // ── Product name tests ────────────────────────────────────────────────

    @Test
    fun `product name is stored correctly`() {
        assertEquals("A4 Paper", normalProduct.productName)
    }

    @Test
    fun `product id is stored correctly`() {
        assertEquals("test-001", normalProduct.id)
    }

    // ── Reorder level tests ───────────────────────────────────────────────

    @Test
    fun `product is not low stock when qty is one above reorder level`() {
        val product = normalProduct.copy(quantityInStock = 21, reorderLevel = 20)
        assertFalse(product.isLowStock)
    }

    @Test
    fun `product is low stock when qty is one below reorder level`() {
        val product = normalProduct.copy(quantityInStock = 19, reorderLevel = 20)
        assertTrue(product.isLowStock)
    }
}