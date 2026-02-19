package com.example.courtgate.framework.remote

import com.example.courtgate.data.datasources.AuthDataSource
import com.example.courtgate.domain.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth
) : AuthDataSource {
    override suspend fun signUp(email: String, password: String): Result<User>{
        return try {
           val result = auth.createUserWithEmailAndPassword(email, password).await()
            val fireUser = result?.user.let { user ->
             User(
                 uid = user?.uid ?: "",
                 email = user?.email ?: ""
             )
            }
           Result.success(fireUser)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User>{
         return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val fireUser = result?.user.let { user ->
                User(
                    uid = user?.uid ?: "",
                    email = user?.email ?: ""
                )
            }
            Result.success(fireUser)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //TODO: mirar como actualizarlo
    override fun getUserId(): String? {
        return auth.currentUser?.uid
    }
}