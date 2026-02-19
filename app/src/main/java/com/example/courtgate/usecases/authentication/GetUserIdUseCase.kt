package com.example.courtgate.usecases.authentication

import com.example.courtgate.data.AuthenticationRepository
import javax.inject.Inject

class GetUserIdUseCase @Inject constructor(private val repository: AuthenticationRepository) {
    operator fun invoke(): String? {
        return repository.getUserId()
    }
}