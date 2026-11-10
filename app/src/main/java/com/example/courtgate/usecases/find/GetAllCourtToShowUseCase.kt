package com.example.courtgate.usecases.find

import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.domain.models.Court
import kotlinx.coroutines.flow.Flow
import java.time.Clock
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetAllCourtToShowUseCase @Inject constructor(
    private val repository: ManageCourtRepository,
    private val clock: Clock
) {

    private val zoneDefault = clock.zone

    operator fun invoke(located: String?, date: ZonedDateTime): Flow<List<Court>> {

        //TODO: optimizable con fun.ext
        val selectedDay = date.toLocalDate().atStartOfDay(zoneDefault).toInstant().toEpochMilli()
        val endSelectedDay =
            date.toLocalDate().plusDays(1).atStartOfDay(zoneDefault).toInstant().toEpochMilli()

        val today = ZonedDateTime.now(clock)
        val currentDayStart = today.toLocalDate().atStartOfDay(zoneDefault).toInstant()
        val endSevenDaysFromNow =
            today.toLocalDate().plusDays(7).atTime(23, 59, 59)
                .atZone(zoneDefault).toInstant()

        //Defensa para no mostrar pistas cuyas únicas horas libres ya han pasado
        val isToday = date.withZoneSameInstant(zoneDefault).toLocalDate()
            .isEqual(today.toLocalDate())
        val minHour = if (isToday) firstBookableHour(today.toLocalTime()) else null

        return repository.getAllCourtToShow(
            located = located,
            selectedDay = selectedDay,
            endSelectedDay = endSelectedDay,
            currentDayStart = currentDayStart,
            endSevenDaysFromNow = endSevenDaysFromNow,
            minHour = minHour
        )
    }

    // Misma regla que GetFreeHoursOnReservedCourtsUseCase: una hora es reservable si no es
    // anterior a "ahora". Se redondea al minuto siguiente para comparar como texto "HH:mm".
    private fun firstBookableHour(now: LocalTime): String {
        val truncated = now.truncatedTo(ChronoUnit.MINUTES)
        if (truncated == now) return truncated.format(HOUR_FORMAT)
        val next = truncated.plusMinutes(1)
        return if (next == LocalTime.MIDNIGHT) END_OF_DAY else next.format(HOUR_FORMAT)
    }

    private companion object {
        val HOUR_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        const val END_OF_DAY = "24:00" // Mayor que cualquier hora "HH:mm" del día
    }
}
