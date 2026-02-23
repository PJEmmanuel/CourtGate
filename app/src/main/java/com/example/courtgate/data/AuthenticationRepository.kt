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

    fun getUserId(): String? {
        return authDataSource.getUserId()
    }

    fun observeAuthState(): Flow<Boolean> = authDataSource.observeAuthState()

    /*suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            Firebase.auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }*/
}