package com.example.courtgate.data

import com.example.courtgate.data.datasources.AuthDataSource
import com.example.courtgate.domain.models.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthenticationRepository @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    suspend fun signUp(email: String, password: String): Result<User> {
        return authDataSource.signUp(email, password)
    }

    suspend fun login(email: String, password: String): Result<User> {
        return authDataSource.login(email, password)
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return authDataSource.sendPasswordResetEmail(email)
    }

    fun isUserLoggedIn(): Boolean {
        return authDataSource.isUserLoggedIn()
    }

    fun observeAuthState(): Flow<Boolean> = authDataSource.observeAuthState()

    fun getCurrentUserId(): String? = authDataSource.getCurrentUserId()

    fun getCurrentUser(): User? = authDataSource.getCurrentUser()

    fun signOut() = authDataSource.signOut()

    suspend fun reauthenticate(password: String): Result<Unit> {
        return authDataSource.reauthenticate(password)
    }

    suspend fun deleteCurrentUser(): Result<Unit> {
        return authDataSource.deleteCurrentUser()
    }
}