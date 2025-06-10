package com.example.courtgate.authentication.domain

interface AuthenticationRepository {
    suspend fun login(email : String, password : String) : Result<Unit>
    suspend fun signUp(email: String, password: String) : Result<Unit>
}