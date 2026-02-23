package com.example.courtgate.data.datasources

import com.example.courtgate.domain.models.User
import kotlinx.coroutines.flow.Flow

interface AuthDataSource {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String): Result<User>
    fun getUserId(): String?
    fun observeAuthState(): Flow<Boolean>
}