package com.example.courtgate.authentication.domain.usecase

import com.example.courtgate.authentication.domain.repository.AuthenticationRepository

class SignUpWhitEmailUseCase(private val repository: AuthenticationRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return repository.signUp(email, password)
    }
}