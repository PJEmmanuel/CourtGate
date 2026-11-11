package com.example.courtgate.data.datasources

import com.example.courtgate.domain.models.User
import kotlinx.coroutines.flow.Flow

interface AuthDataSource {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String): Result<User>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    fun isUserLoggedIn(): Boolean
    fun observeAuthState(): Flow<Boolean>
    fun getCurrentUserId(): String?
    fun getCurrentUser(): User?
    fun signOut()
    suspend fun reauthenticate(password: String): Result<Unit>
    suspend fun deleteCurrentUser(): Result<Unit>
}
