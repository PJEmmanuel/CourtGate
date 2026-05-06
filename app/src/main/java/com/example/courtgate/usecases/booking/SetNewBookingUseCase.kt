package com.example.courtgate.usecases.booking

import com.example.courtgate.ResultManage
import com.example.courtgate.data.AuthenticationRepository
import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.NewCourtBooking
import java.time.Instant
import javax.inject.Inject

class SetNewBookingUseCase @Inject constructor(
    val managerCourtRepository: ManageCourtRepository,
    val authRepository: AuthenticationRepository
) {
    suspend operator fun invoke(
        code: String,
        date: Long,
        hour: String,
    ): ResultManage<Unit, DomainError> {

        //TODO: Extraer a fun.ext  Revisar que entra un Long al useCase
        val dateToInstant = Instant.ofEpochMilli(date)

        val userId = authRepository.getCurrentUserId()
            ?: return ResultManage.Failure(DomainError.Auth.UserNotFound)
        val newBooking =
            NewCourtBooking(code = code, date = dateToInstant, hour = hour, userId = userId)
        return managerCourtRepository.setBooking(newBooking)
    }
}