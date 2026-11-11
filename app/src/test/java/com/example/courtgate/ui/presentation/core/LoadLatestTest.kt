package com.example.courtgate.ui.presentation.core

import app.cash.turbine.test
import com.example.courtgate.ResultCourt
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.domain.models.isRetryable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoadLatestTest {

    private fun failing(error: DomainError): Flow<String> = flow { throw DomainException(error) }

    @Test
    fun `counts consecutive failures, shows Loading on retry and resets after success`() = runTest {
        val retryAttempts = MutableStateFlow(0)
        val loads = listOf(
            failing(DomainError.Remote.ServerError),
            flow { throw RuntimeException("sin mapear") },
            flowOf("ok"),
            failing(DomainError.Remote.ServerError),
        )
        var call = 0

        retryAttempts.loadLatest(DomainError.Remote.UnknownRemoteError) { loads[call++] }.test {
            assertEquals(ResultCourt.Error(DomainError.Remote.ServerError, 1), awaitItem())

            retryAttempts.value = 1
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Error(DomainError.Remote.UnknownRemoteError, 2), awaitItem())

            retryAttempts.value = 2
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Success("ok"), awaitItem())

            // Tras un Success no se emite Loading y el contador vuelve a empezar
            retryAttempts.value = 3
            assertEquals(ResultCourt.Error(DomainError.Remote.ServerError, 1), awaitItem())
        }
    }

    @Test
    fun `transient errors are retryable and permanent ones are not`() {
        assertTrue(DomainError.Remote.ServerError.isRetryable)
        assertTrue(DomainError.Auth.NetworkError.isRetryable)
        assertFalse(DomainError.Remote.AccessDenied.isRetryable)
        assertFalse(DomainError.Auth.SessionExpired.isRetryable)
        assertFalse(DomainError.Local.DatabaseCorrupted.isRetryable)
    }
}
