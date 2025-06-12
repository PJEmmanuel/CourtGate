package com.example.courtgate.authentication.domain.usecase

import com.example.courtgate.authentication.domain.matcher.EmailMatcher
import com.example.courtgate.authentication.domain.repository.AuthenticationRepository

class LoginWithEmailUseCase(private val repository: AuthenticationRepository) {
    suspend operator fun invoke(email: String, password: String):Result<Unit> {
        return repository.login(email, password)
    }
}