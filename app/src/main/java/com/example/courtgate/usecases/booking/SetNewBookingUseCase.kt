package com.example.courtgate.usecases.booking

import com.example.courtgate.ResultManage
import com.example.courtgate.data.AuthenticationRepository
import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.NewCourtBooking
import java.time.Clock
import java.time.Instant
import java.time.LocalTime
import javax.inject.Inject

class SetNewBookingUseCase @Inject constructor(
    private val managerCourtRepository: ManageCourtRepository,
    private val authRepository: AuthenticationRepository,
    private val clock: Clock
) {
    suspend operator fun invoke(
        code: String,
        date: Long,
        hour: String,
    ): ResultManage<Unit, DomainError> {

        //TODO: Extraer a fun.ext  Revisar que entra un Long al useCase
        val userId = authRepository.getCurrentUserId()
            ?: return ResultManage.Failure(DomainError.Auth.UserNotFound)

        val zone = clock.zone
        val dateInstant = Instant.ofEpochMilli(date)
        val slotDate = dateInstant.atZone(zone).toLocalDate()
        val slotTime = LocalTime.parse(hour)
        val slotInstant = slotDate.atTime(slotTime).atZone(zone).toInstant()

        // no permitir reservas en el pasado
        if (slotInstant.isBefore(Instant.now(clock))) {
            return ResultManage.Failure(DomainError.Booking.SlotInPast)
        }

        val newBooking =
            NewCourtBooking(
                code = code,
                date = dateInstant,
                hour = hour,
                userId = userId,
                slotInstant
            )
        return managerCourtRepository.setBooking(newBooking)
    }
}