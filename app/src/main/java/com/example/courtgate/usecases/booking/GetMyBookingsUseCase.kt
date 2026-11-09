package com.example.courtgate.usecases.booking

import com.example.courtgate.data.AuthenticationRepository
import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

class GetMyBookingsUseCase @Inject constructor(
    private val repository: ManageCourtRepository,
    private val authRepository: AuthenticationRepository,
    private val clock: Clock
) {

    // La comprobación va dentro del flow para que el error llegue al .catch del ViewModel
    operator fun invoke(): Flow<List<CourtBooking>> = flow {

        val userId = authRepository.getCurrentUserId()
            ?: throw DomainException(DomainError.Auth.NetworkError)

        val startAt = Instant.now(clock)

        emitAll(
            repository.getMyBookings(
                currentUser = userId,
                startAt = startAt
            )
        )
    }
}