package com.example.courtgate.authentication.data

import com.example.courtgate.authentication.domain.AutheticationRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthenticationRepositoryImpl : AutheticationRepository {
    override suspend fun login(email: String, password: String): Result<Unit> {
       return try {
            Firebase.auth.signInWithEmailAndPassword(email,password).await()
           Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}