package com.codecrafters.inventorypro

import com.codecrafters.inventorypro.models.Sale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SaleModelTest {

    private lateinit var sale: Sale
    private lateinit var largeSale: Sale

    @Before
    fun setup() {
        sale = Sale(
            id           = "sale-001",
            productId    = "prod-101",
            productName  = "A4 Paper",
            quantitySold = 5,
            unitPrice    = 45.00,
            totalPrice   = 225.00,
            saleDate     = "2025-01-10",
            servedBy     = "Kago Khumalo",
            userRole     = "Shop Assistant"
        )

        largeSale = Sale(
            id           = "sale-002",
            productId    = "prod-103",
            productName  = "Filing Cabinet",
            quantitySold = 2,
            unitPrice    = 850.00,
            totalPrice   = 1700.00,
            saleDate     = "2025-01-11",
            servedBy     = "Lefa Mokoena",
            userRole     = "Manager"
        )
    }

    // ── formattedTotal tests ──────────────────────────────────────────────

    @Test
    fun `formatted total includes R prefix`() {
        assertTrue(sale.formattedTotal.startsWith("R"))
    }

    @Test
    fun `formatted total shows correct amount`() {
        assertEquals("R 225.00", sale.formattedTotal)
    }

    @Test
    fun `formatted total for large sale is correct`() {
        assertEquals("R 1700.00", largeSale.formattedTotal)
    }

    // ── formattedUnitPrice tests ──────────────────────────────────────────

    @Test
    fun `formatted unit price includes R prefix`() {
        assertTrue(sale.formattedUnitPrice.startsWith("R"))
    }

    @Test
    fun `formatted unit price shows correct amount`() {
        assertEquals("R 45.00", sale.formattedUnitPrice)
    }

    // ── Sale data tests ───────────────────────────────────────────────────

    @Test
    fun `sale id is stored correctly`() {
        assertEquals("sale-001", sale.id)
    }

    @Test
    fun `product name is stored correctly`() {
        assertEquals("A4 Paper", sale.productName)
    }

    @Test
    fun `quantity sold is stored correctly`() {
        assertEquals(5, sale.quantitySold)
    }

    @Test
    fun `served by is stored correctly`() {
        assertEquals("Kago Khumalo", sale.servedBy)
    }

    @Test
    fun `sale date is stored correctly`() {
        assertEquals("2025-01-10", sale.saleDate)
    }

    @Test
    fun `total price matches expected calculation`() {
        val expected = sale.quantitySold * sale.unitPrice
        assertEquals(expected, sale.totalPrice, 0.001)
    }

    @Test
    fun `large sale total is correct`() {
        val expected = 2 * 850.00
        assertEquals(expected, largeSale.totalPrice, 0.001)
    }
}