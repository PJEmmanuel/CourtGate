package com.example.courtgate.usecases.find

import com.example.courtgate.home.domain.models.CourtList
import com.example.courtgate.home.domain.repository.HomeRepository
import java.time.ZoneId
import java.time.ZonedDateTime

class GetAllCourtToShowUseCase(private val repository: HomeRepository) {
    suspend operator fun invoke(date: ZonedDateTime): Result<List<CourtList>> {
        val offeredHours = listOf("08:00", "09:30", "11:00", "12:30", "16:00", "17:30")
        //val offeredHours = listOf("16:00")

        //TODO: Hacer una constant !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        val zone = ZoneId.of("Europe/Madrid")
        // convertir fecha → rango del día completo
        val startOfDay = date.toLocalDate().atStartOfDay(zone).toInstant()
        val endOfDay = date.toLocalDate().plusDays(1).atStartOfDay(zone).toInstant()

        val allCourt = repository.getAllCourtToShow()
        val allBooking = repository.getBookingsByDate(startOfDay = startOfDay, endOfDay = endOfDay)

        if (allCourt.isFailure || allBooking.isFailure) {
            return Result.failure(Exception("Error fetching data"))
        }

        val courts = allCourt.getOrThrow()
        val booking = allBooking.getOrThrow()

        val availableCourts = courts.filter { court ->
            val courtBookings = booking.filter { it.code == court.code }
            val reservedHours = courtBookings.map { it.hour }


            val freeHours = offeredHours.filter { it !in reservedHours }

            freeHours.isNotEmpty()
        }


        return Result.success(availableCourts)

    }
}