package com.example.courtgate.usecases.authentication

import com.example.courtgate.data.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val repository: AuthenticationRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.observeAuthState()
}
