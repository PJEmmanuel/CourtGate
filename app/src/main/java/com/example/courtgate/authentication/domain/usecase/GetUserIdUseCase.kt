package com.example.courtgate.authentication.domain.usecase

import com.example.courtgate.authentication.domain.repository.AuthenticationRepository

class GetUserIdUseCase(private val repository: AuthenticationRepository) {
    operator fun invoke(): String? {
        return repository.getUserId()
    }
}