package com.example.courtgate.usecases.settings

import com.example.courtgate.data.AuthenticationRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val authRepository: AuthenticationRepository) {
    operator fun invoke() = authRepository.signOut()
}
