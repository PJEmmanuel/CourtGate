package com.example.courtgate.usecases.authentication

import com.example.courtgate.data.AuthenticationRepository
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(private val repository: AuthenticationRepository) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return repository.sendPasswordResetEmail(email)
    }
}
