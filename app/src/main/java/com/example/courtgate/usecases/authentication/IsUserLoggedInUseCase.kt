package com.example.courtgate.usecases.authentication

import com.example.courtgate.data.AuthenticationRepository
import javax.inject.Inject

class IsUserLoggedInUseCase @Inject constructor(private val repository: AuthenticationRepository) {
    operator fun invoke(): Boolean {
        return repository.isUserLoggedIn()
    }
}