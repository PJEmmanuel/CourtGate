package com.example.courtgate.framework

import com.example.courtgate.data.datasources.AuthDataSource
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.domain.models.User
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.Instant
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
            } ?: return Result.failure(DomainException(DomainError.Auth.UnknownAuthError))
            Result.success(fireUser)

        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(DomainException(e.toAuthError()))
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
            } ?: return Result.failure(DomainException(DomainError.Auth.UnknownAuthError))
            Result.success(fireUser)

        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(DomainException(e.toAuthError()))
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(DomainException(e.toAuthError()))
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

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    override fun getCurrentUser(): User? {
        return auth.currentUser?.let { user ->
            User(
                uid = user.uid,
                email = user.email.orEmpty(),
                createdAt = user.metadata?.creationTimestamp?.let(Instant::ofEpochMilli)
            )
        }
    }

    override fun signOut() = auth.signOut()

    // Borrar la cuenta exige un inicio de sesión reciente: se vuelve a pedir la contraseña
    override suspend fun reauthenticate(password: String): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(DomainException(DomainError.Auth.SessionExpired))
            val email = user.email
                ?: return Result.failure(DomainException(DomainError.Auth.SessionExpired))
            user.reauthenticate(EmailAuthProvider.getCredential(email, password)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(DomainException(e.toAuthError()))
        }
    }

    override suspend fun deleteCurrentUser(): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(DomainException(DomainError.Auth.SessionExpired))
            user.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(DomainException(e.toAuthError()))
        }
    }

    // WeakPassword hereda de InvalidCredentials y RecentLoginRequired de InvalidUser:
    // tienen que ir antes
    private fun Exception.toAuthError(): DomainError.Auth =
        when (this) {
            is FirebaseAuthWeakPasswordException -> DomainError.Auth.WeakPassword
            is FirebaseAuthRecentLoginRequiredException -> DomainError.Auth.RecentLoginRequired
            is FirebaseAuthInvalidCredentialsException -> DomainError.Auth.InvalidCredentials
            is FirebaseAuthInvalidUserException -> DomainError.Auth.UserNotFound
            is FirebaseAuthUserCollisionException -> DomainError.Auth.UserCollision
            is FirebaseNetworkException -> DomainError.Auth.NetworkError
            is FirebaseTooManyRequestsException -> DomainError.Auth.TooManyRequests
            else -> DomainError.Auth.UnknownAuthError
        }
}
