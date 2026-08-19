package com.codecrafters.inventorypro.models

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: String = "",
    val createdAt: String = ""
) {
    fun isManager(): Boolean = role == "Manager"

    companion object {
        const val ROLE_MANAGER = "Manager"
        const val ROLE_SHOP_ASSISTANT = "Shop Assistant"
    }
}