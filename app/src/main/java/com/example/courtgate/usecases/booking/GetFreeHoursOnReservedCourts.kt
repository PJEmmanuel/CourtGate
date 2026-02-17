package com.example.courtgate.usecases.booking

import android.util.Log
import com.example.courtgate.domain.models.FreeHoursOfCourt
import com.example.courtgate.data.HomeRepository
import java.time.ZoneId
import java.time.ZonedDateTime

class GetFreeHoursOnReservedCourts(private val repository: HomeRepository) {

    suspend operator fun invoke(code: String, date: String): Result<List<FreeHoursOfCourt>> {

        val offeredHours = listOf("08:00", "09:30", "11:00", "12:30", "16:00", "17:30") //TODO poner una CONST CORE

        /*val zone = ZoneId.of("Europe/Madrid")
        val localDate = ZonedDateTime.parse(date).withZoneSameInstant(zone).toLocalDate()

        val startOfDay = localDate.atStartOfDay(zone).toInstant()
        val endOfDay = localDate.plusDays(1).atStartOfDay(zone).toInstant()*/

        //TODO: 1 Comprobar que el date que se trae de Find, está con la hora correcta, ya que
        // quizás sobre el ajuste.
        // 2 nuevo error en firestore
        // 3

        val dateToZonedDateTime = ZonedDateTime.parse(date)
        val zone = ZoneId.of("Europe/Madrid")

        // convertir fecha → rango del día completo
        val startOfDay = dateToZonedDateTime.toLocalDate().atStartOfDay(zone).toInstant()
        val endOfDay = dateToZonedDateTime.toLocalDate().plusDays(1).atStartOfDay(zone).toInstant()

        val allBookingReserved = repository.getFreeHoursOnReservedCourts(code = code, startOfDay = startOfDay, endOfDay = endOfDay)

        Log.i("freeHours", allBookingReserved.toString())


        return allBookingReserved.map { bookings ->
            Log.i("freeHours", allBookingReserved.toString())

            Log.i("freeHours", offeredHours.toString())


            offeredHours.map { hour ->
                val isReserved = bookings.any { it.hour == hour }
                Log.i("freeHours", isReserved.toString())

                FreeHoursOfCourt(
                    code = code,
                    hour = hour,
                    isFree = isReserved,
                    isSelected = false
                )
            }
        }
    }
}