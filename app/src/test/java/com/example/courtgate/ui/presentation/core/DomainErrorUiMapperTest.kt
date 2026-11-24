package com.example.courtgate.ui.presentation.core

import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import org.junit.Assert.assertEquals
import org.junit.Test

class DomainErrorUiMapperTest {

    @Test
    fun `DomainException keeps its own error`() {
        val error = DomainException(DomainError.Auth.InvalidCredentials)
            .toDomainError(DomainError.Auth.UnknownAuthError)

        assertEquals(DomainError.Auth.InvalidCredentials, error)
    }

    @Test
    fun `any other exception becomes the screen's unknown error`() {
        assertEquals(
            DomainError.Auth.UnknownAuthError,
            IllegalStateException().toDomainError(DomainError.Auth.UnknownAuthError)
        )
        assertEquals(
            DomainError.Remote.UnknownRemoteError,
            RuntimeException().toDomainError(DomainError.Remote.UnknownRemoteError)
        )
    }
}
