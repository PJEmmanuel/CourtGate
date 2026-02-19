package com.example.courtgate.usecases.authentication

import com.example.courtgate.data.AuthenticationRepository
import com.example.courtgate.domain.models.User
import javax.inject.Inject

class LoginWithEmailUseCase @Inject constructor(private val repository: AuthenticationRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return repository.login(email, password)
    }
}