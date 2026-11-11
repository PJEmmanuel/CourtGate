package com.example.courtgate.usecases.settings

import com.example.courtgate.data.AuthenticationRepository
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.domain.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthenticationRepository
) {
    // La comprobación va dentro del flow para que el error llegue al .catch del ViewModel
    operator fun invoke(): Flow<User> = flow {
        emit(authRepository.getCurrentUser() ?: throw DomainException(DomainError.Auth.SessionExpired))
    }
}
