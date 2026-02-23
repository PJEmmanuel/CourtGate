package com.example.courtgate.framework

import com.example.courtgate.data.datasources.AuthDataSource
import com.example.courtgate.domain.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth
) : AuthDataSource {
    override suspend fun signUp(email: String, password: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val fireUser = result?.user?.let { user ->
                User(
                    uid = user.uid,
                    email = user.email ?: ""
                )
            } ?: return Result.failure(Exception("Sign up failed: user is null"))
            Result.success(fireUser)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val fireUser = result?.user?.let { user ->
                User(
                    uid = user.uid,
                    email = user.email ?: ""
                )
            } ?: return Result.failure(Exception("Login failed: user is null"))
            Result.success(fireUser)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isUserLoggedIn(): Boolean = auth.currentUser != null


    override fun observeAuthState(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser != null)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }
}