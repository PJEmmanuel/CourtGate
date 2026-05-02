package com.example.courtgate.usecases.booking

import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.domain.models.FreeHoursOfCourt
import kotlinx.coroutines.flow.Flow
import java.time.Clock
import java.time.Instant
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class GetFreeHoursOnReservedCourtsUseCase @Inject constructor(
    private val repository: ManageCourtRepository,
    private val clock: Clock
) {

    private val zoneDefault = clock.zone

    @OptIn(ExperimentalTime::class)
    operator fun invoke(code: String, selectedDay: Long): Flow<List<FreeHoursOfCourt>> {

        val dayStart = Instant.ofEpochMilli(selectedDay)
        val endSelectedDay = dayStart
            .atZone(zoneDefault).toLocalDate().plusDays(1).atStartOfDay(zoneDefault)
            .toInstant()
            .toEpochMilli()
        val today = ZonedDateTime.now(clock)
        val currentDayStart = today.toLocalDate().atStartOfDay(zoneDefault).toInstant()
        val endSevenDaysFromNow =
            today.toLocalDate().plusDays(7).atTime(23, 59, 59)
                .atZone(zoneDefault).toInstant()

        return repository.getHoursWithAvailability(
            code = code,
            dayStart = selectedDay,
            dayEnd = endSelectedDay,
            currentDayStart = currentDayStart,
            endSevenDaysFromNow = endSevenDaysFromNow
        )
    }

    /* suspend operator fun invoke(code: String, date: String): Result<List<FreeHoursOfCourt>> {

         val offeredHours = listOf("08:00", "09:30", "11:00", "12:30", "16:00", "17:30") //TODO poner una CONST CORE

 val zone = ZoneId.of("Europe/Madrid")
         val localDate = ZonedDateTime.parse(date).withZoneSameInstant(zone).toLocalDate()

         val startOfDay = localDate.atStartOfDay(zone).toInstant()
         val endOfDay = localDate.plusDays(1).atStartOfDay(zone).toInstant()


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
     }*/
}
