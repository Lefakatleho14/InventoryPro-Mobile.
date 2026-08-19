package com.codecrafters.inventorypro.repository

import com.codecrafters.inventorypro.models.User
import com.codecrafters.inventorypro.utils.FirestoreHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    // ── Login with email and password ─────────────────────────────────────
    suspend fun loginWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid    = result.user?.uid ?: return Result.failure(Exception("Login failed"))
            fetchUserProfile(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Register new user ─────────────────────────────────────────────────
    suspend fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid    = result.user?.uid ?: return Result.failure(Exception("Registration failed"))

            // Create Firestore profile
            val user = User(
                uid      = uid,
                fullName = fullName,
                email    = email,
                role     = User.ROLE_SHOP_ASSISTANT,
                createdAt = getCurrentDate()
            )

            db.collection(FirestoreHelper.COLLECTION_USERS)
                .document(uid)
                .set(userToMap(user))
                .await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Google Sign-In ────────────────────────────────────────────────────
    suspend fun loginWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result     = auth.signInWithCredential(credential).await()
            val uid        = result.user?.uid ?: return Result.failure(Exception("Google login failed"))
            val isNew      = result.additionalUserInfo?.isNewUser ?: false

            if (isNew) {
                // Create Firestore profile for new Google users
                val user = User(
                    uid      = uid,
                    fullName = result.user?.displayName ?: "User",
                    email    = result.user?.email ?: "",
                    role     = User.ROLE_SHOP_ASSISTANT,
                    createdAt = getCurrentDate()
                )
                db.collection(FirestoreHelper.COLLECTION_USERS)
                    .document(uid)
                    .set(userToMap(user))
                    .await()
                Result.success(user)
            } else {
                fetchUserProfile(uid)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Fetch user profile from Firestore ─────────────────────────────────
    suspend fun fetchUserProfile(uid: String): Result<User> {
        return try {
            val doc = db.collection(FirestoreHelper.COLLECTION_USERS)
                .document(uid)
                .get()
                .await()

            if (doc.exists()) {
                val user = User(
                    uid      = uid,
                    fullName = doc.getString(FirestoreHelper.FIELD_FULL_NAME) ?: "",
                    email    = doc.getString(FirestoreHelper.FIELD_EMAIL) ?: "",
                    role     = doc.getString(FirestoreHelper.FIELD_ROLE) ?: User.ROLE_SHOP_ASSISTANT,
                    createdAt = doc.getString(FirestoreHelper.FIELD_CREATED_AT) ?: ""
                )
                Result.success(user)
            } else {
                Result.failure(Exception("User profile not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Change password ───────────────────────────────────────────────────
    suspend fun changePassword(newPassword: String): Result<Unit> {
        return try {
            auth.currentUser?.updatePassword(newPassword)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────
    fun logout() = auth.signOut()

    // ── Helpers ───────────────────────────────────────────────────────────
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    private fun userToMap(user: User): Map<String, Any> = mapOf(
        FirestoreHelper.FIELD_FULL_NAME  to user.fullName,
        FirestoreHelper.FIELD_EMAIL      to user.email,
        FirestoreHelper.FIELD_ROLE       to user.role,
        FirestoreHelper.FIELD_CREATED_AT to user.createdAt
    )

    private fun getCurrentDate(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}