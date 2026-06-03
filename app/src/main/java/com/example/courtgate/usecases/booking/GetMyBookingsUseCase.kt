package com.example.courtgate.usecases.booking

import com.example.courtgate.data.AuthenticationRepository
import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import kotlinx.coroutines.flow.Flow
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

class GetMyBookingsUseCase @Inject constructor(
    private val repository: ManageCourtRepository,
    private val authRepository: AuthenticationRepository,
    private val clock: Clock
) {

    operator fun invoke(): Flow<List<CourtBooking>> {

        val userId = authRepository.getCurrentUserId()

        val startAt = Instant.now(clock)

        return if (userId != null) {
            repository.getMyBookings(
                currentUser = userId,
                startAt = startAt
            )
        } else throw DomainException(DomainError.Auth.NetworkError)
    }
}