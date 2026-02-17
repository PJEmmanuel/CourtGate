package com.example.courtgate.usecases.authentication

import com.example.courtgate.data.AuthenticationRepository

class SignUpWhitEmailUseCase(private val repository: AuthenticationRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return repository.signUp(email, password)
    }
}