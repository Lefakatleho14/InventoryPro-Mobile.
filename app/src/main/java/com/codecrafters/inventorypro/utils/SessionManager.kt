package com.codecrafters.inventorypro.utils

import android.content.Context
import android.content.SharedPreferences
import com.codecrafters.inventorypro.models.User

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREF_NAME, Context.MODE_PRIVATE
    )

    companion object {
        private const val PREF_NAME     = "InventoryProSession"
        private const val KEY_UID       = "uid"
        private const val KEY_EMAIL     = "email"
        private const val KEY_FULL_NAME = "fullName"
        private const val KEY_ROLE      = "role"
        private const val KEY_LOGGED_IN = "isLoggedIn"
    }

    fun saveSession(user: User) {
        prefs.edit().apply {
            putString(KEY_UID, user.uid)
            putString(KEY_EMAIL, user.email)
            putString(KEY_FULL_NAME, user.fullName)
            putString(KEY_ROLE, user.role)
            putBoolean(KEY_LOGGED_IN, true)
            apply()
        }
    }

    fun getUser(): User = User(
        uid      = prefs.getString(KEY_UID, "") ?: "",
        email    = prefs.getString(KEY_EMAIL, "") ?: "",
        fullName = prefs.getString(KEY_FULL_NAME, "") ?: "",
        role     = prefs.getString(KEY_ROLE, "") ?: ""
    )

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false)

    fun isManager(): Boolean = prefs.getString(KEY_ROLE, "") == User.ROLE_MANAGER

    fun getFullName(): String = prefs.getString(KEY_FULL_NAME, "") ?: ""

    fun getRole(): String = prefs.getString(KEY_ROLE, "") ?: ""

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}