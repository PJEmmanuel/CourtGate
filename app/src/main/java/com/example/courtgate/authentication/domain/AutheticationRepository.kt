package com.example.courtgate.authentication.domain

interface AutheticationRepository {
    suspend fun login(email : String, password : String) : Result<Unit>
}