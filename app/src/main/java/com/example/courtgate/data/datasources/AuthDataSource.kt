package com.example.courtgate.data.datasources

import com.example.courtgate.domain.models.User

interface AuthDataSource {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String): Result<User>
    fun getUserId(): String?
}