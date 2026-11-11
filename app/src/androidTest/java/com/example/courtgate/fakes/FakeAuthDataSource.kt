package com.example.courtgate.fakes

import com.example.courtgate.data.datasources.AuthDataSource
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.domain.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Firebase Auth en memoria: una sola cuenta válida (TEST_EMAIL / TEST_PASSWORD).
// Los errores se devuelven igual que FirebaseAuthDataSource: Result.failure(DomainException).
@Singleton
class FakeAuthDataSource @Inject constructor() : AuthDataSource {

    private val currentUser = MutableStateFlow<User?>(null)

    fun logInAsTestUser() {
        currentUser.value = TEST_USER
    }

    override suspend fun login(email: String, password: String): Result<User> =
        if (email == TEST_EMAIL && password == TEST_PASSWORD) {
            currentUser.value = TEST_USER
            Result.success(TEST_USER)
        } else {
            Result.failure(DomainException(DomainError.Auth.InvalidCredentials))
        }

    override suspend fun signUp(email: String, password: String): Result<User> =
        Result.failure(DomainException(DomainError.Auth.UserCollision))

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = Result.success(Unit)

    override fun isUserLoggedIn(): Boolean = currentUser.value != null

    override fun observeAuthState(): Flow<Boolean> = currentUser.map { it != null }

    override fun getCurrentUserId(): String? = currentUser.value?.uid

    override fun getCurrentUser(): User? = currentUser.value

    override fun signOut() {
        currentUser.value = null
    }

    override suspend fun reauthenticate(password: String): Result<Unit> =
        if (password == TEST_PASSWORD) Result.success(Unit)
        else Result.failure(DomainException(DomainError.Auth.InvalidCredentials))

    override suspend fun deleteCurrentUser(): Result<Unit> {
        currentUser.value = null
        return Result.success(Unit)
    }

    companion object {
        const val TEST_EMAIL = "tester@courtgate.test"
        const val TEST_PASSWORD = "Test1234"
        val TEST_USER = User(uid = "test-uid", email = TEST_EMAIL)
    }
}
