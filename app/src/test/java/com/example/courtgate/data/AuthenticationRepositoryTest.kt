package com.example.courtgate.data

import com.example.courtgate.data.datasources.AuthDataSource
import com.example.courtgate.domain.models.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class AuthenticationRepositoryTest {

    @Mock
    lateinit var authDataSource: AuthDataSource

    private lateinit var repository: AuthenticationRepository

    private val email = "asd@ASD.com"
    private val pass = "asdASD123"

    @Before
    fun setUp() {
        repository = AuthenticationRepository(
            authDataSource = authDataSource
        )
    }

    @Test
    fun `signUp is correct, then Result return User`(): Unit = runTest {
        val uid = "asd123"
        whenever(authDataSource.signUp(email, pass))
            .thenReturn(Result.success(User(uid, email)))

        val repository = repository.signUp(email, pass)

        assertEquals(Result.success(User(uid, email)), repository)
        assertEquals(true, repository.isSuccess)
    }

    @Test
    fun `signUp is failure, then Result return exception`(): Unit = runTest {
        val errorMessage = "Fail"
        val error = Exception(errorMessage)
        whenever(authDataSource.signUp(email, pass))
            .thenReturn(Result.failure(Exception(errorMessage)))

        val repository = repository.signUp(email, pass)

        assertEquals(error.message, repository.exceptionOrNull()?.message)
        assertEquals(true, repository.isFailure)

    }

    @Test
    fun `login is success, then Result return success`(): Unit = runTest {
        val uid = "asd123"
        whenever(authDataSource.login(email, pass))
            .thenReturn(Result.success(User(uid, email)))

        val repository = repository.login(email, pass)

        assertEquals(Result.success(User(uid, email)), repository)
        assertEquals(true, repository.isSuccess)
    }

    @Test
    fun `login is failure, then Result return exception`(): Unit = runTest {
        val errorMessage = "Fail"
        val error = Exception(errorMessage)
        whenever(authDataSource.login(email, pass))
            .thenReturn(Result.failure(Exception(errorMessage)))

        val repository = repository.login(email, pass)

        assertEquals(error.message, repository.exceptionOrNull()?.message)
        assertEquals(true, repository.isFailure)

    }

    @Test
    fun `get true when user is logged in`() {
        whenever(authDataSource.isUserLoggedIn()).thenReturn(true)

        val repository = repository.isUserLoggedIn()

        assertTrue(repository)
    }

    @Test
    fun `get false when user is not logged in`() {
        whenever(authDataSource.isUserLoggedIn()).thenReturn(false)

        val repository = repository.isUserLoggedIn()

        assertFalse(repository)
    }

    @Test
    fun `get true while user is logged in`(): Unit = runTest {
        whenever(authDataSource.observeAuthState()).thenReturn(flow { emit(true) })

        val repository = repository.observeAuthState().first()

        assertTrue(repository)
    }

    @Test
    fun `get false while user is not logged in or logged out`(): Unit = runTest {
        whenever(authDataSource.observeAuthState()).thenReturn(flow { emit(false) })

        val repository = repository.observeAuthState().first()

        assertFalse(repository)
    }
}