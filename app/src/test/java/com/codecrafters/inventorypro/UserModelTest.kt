package com.codecrafters.inventorypro

import com.codecrafters.inventorypro.models.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserModelTest {

    private lateinit var manager: User
    private lateinit var shopAssistant: User

    @Before
    fun setup() {
        manager = User(
            uid      = "uid-001",
            fullName = "Lefa Mokoena",
            email    = "lefa@inventorypro.com",
            role     = "Manager",
            createdAt = "2025-01-01"
        )

        shopAssistant = User(
            uid      = "uid-002",
            fullName = "Kago Khumalo",
            email    = "kago@inventorypro.com",
            role     = "Shop Assistant",
            createdAt = "2025-01-01"
        )
    }

    // ── isManager tests ───────────────────────────────────────────────────

    @Test
    fun `manager role returns true for isManager`() {
        assertTrue(manager.isManager())
    }

    @Test
    fun `shop assistant role returns false for isManager`() {
        assertFalse(shopAssistant.isManager())
    }

    @Test
    fun `empty role returns false for isManager`() {
        val user = User(role = "")
        assertFalse(user.isManager())
    }

    @Test
    fun `lowercase manager role returns false for isManager`() {
        val user = User(role = "manager")
        assertFalse(user.isManager())
    }

    // ── Role constants tests ──────────────────────────────────────────────

    @Test
    fun `manager role constant is correct`() {
        assertEquals("Manager", User.ROLE_MANAGER)
    }

    @Test
    fun `shop assistant role constant is correct`() {
        assertEquals("Shop Assistant", User.ROLE_SHOP_ASSISTANT)
    }

    // ── User data tests ───────────────────────────────────────────────────

    @Test
    fun `user fullName is stored correctly`() {
        assertEquals("Lefa Mokoena", manager.fullName)
    }

    @Test
    fun `user email is stored correctly`() {
        assertEquals("lefa@inventorypro.com", manager.email)
    }

    @Test
    fun `user uid is stored correctly`() {
        assertEquals("uid-001", manager.uid)
    }

    @Test
    fun `shop assistant name is stored correctly`() {
        assertEquals("Kago Khumalo", shopAssistant.fullName)
    }

    @Test
    fun `default user has empty role`() {
        val user = User()
        assertEquals("", user.role)
    }
}