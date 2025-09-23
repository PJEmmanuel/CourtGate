package com.example.courtgate.home.domain.usecase

import android.util.Log
import com.example.courtgate.home.domain.models.CourtList
import com.example.courtgate.home.domain.repository.HomeRepository
import java.time.ZoneId
import java.time.ZonedDateTime

class GetAllCourtToShowUseCase(private val repository: HomeRepository) {
    suspend operator fun invoke(date: ZonedDateTime): Result<List<CourtList>> {
        //val offeredHours = listOf("08:00", "09:30", "11:00", "12:30", "16:00", "17:30")
        val offeredHours = listOf("16:00")

        val zone = ZoneId.of("Europe/Madrid")
        // convertir fecha → rango del día completo
        val startOfDay = date.toLocalDate().atStartOfDay(zone).toInstant()
        val endOfDay = date.toLocalDate().plusDays(1).atStartOfDay(zone).toInstant()

        val allCourt = repository.getAllCourtToShow()
        val allBooking = repository.getBookingsByDate(startOfDay = startOfDay, endOfDay = endOfDay)

        Log.i("getData", "Lista de reservas: $allBooking")
        Log.i("getData", "Lista de Pistas: $allCourt")
        if (allCourt.isFailure || allBooking.isFailure) {
            return Result.failure(Exception("Error fetching data"))
        }

        val courts = allCourt.getOrThrow()
        val booking = allBooking.getOrThrow()

        val availableCourts = courts.filter { court ->
            val courtBookings = booking.filter { it.code == court.code }
            val reservedHours = courtBookings.map { it.hour }
            Log.i("getData", "Lista de courtboo: $courtBookings")
            Log.i("getData", "Lista de reserHour: $reservedHours")

            val freeHours = offeredHours.filter { it !in reservedHours }
            Log.i("getData", "Lista de freehour: $freeHours")
            freeHours.isNotEmpty()
        }
        Log.i("getData", "Lista de avaible: $availableCourts")

        return Result.success(availableCourts)

    }
}

/*class GetAllCourtToShowUseCase(private val repository: HomeRepository) {
    suspend operator fun invoke(): Result<List<CourtList>> {
        return repository.getAllCourtToShow()

    }
}*/

/*class GetAllCourtToShowUseCase(private val repository: HomeRepository) {
    suspend operator fun invoke(location: String? = null): Result<List<CourtList>> {
        val result = repository.getAllCourtToShow()

        return result.mapCatching { courts ->
            if (location.isNullOrEmpty()) {
                courts
            } else {
                courts.filter { it.located == location }
            }
        }
    }
}*/