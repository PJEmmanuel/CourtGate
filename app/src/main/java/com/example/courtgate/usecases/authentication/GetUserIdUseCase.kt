package com.example.courtgate.usecases.authentication

import com.example.courtgate.data.AuthenticationRepository

class GetUserIdUseCase(private val repository: AuthenticationRepository) {
    operator fun invoke(): String? {
        return repository.getUserId()
    }
}