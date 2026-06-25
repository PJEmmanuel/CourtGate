package com.example.courtgate.usecases.booking

import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.domain.models.FreeHoursOfCourt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock
import java.time.Instant
import java.time.LocalTime
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime

class GetFreeHoursOnReservedCourtsUseCase @Inject constructor(
    private val repository: ManageCourtRepository,
    private val clock: Clock
) {

    private val zoneDefault = clock.zone

    @OptIn(ExperimentalTime::class)
    operator fun invoke(code: String, selectedDay: Long): Flow<List<FreeHoursOfCourt>> {

        //TODO: optimizable con fun.ext

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

        //Defensa para no reservar horas del pasado
        val isToday = Instant.ofEpochMilli(selectedDay)
            .atZone(zoneDefault)
            .toLocalDate()
            .isEqual(today.toLocalDate())
        val nowTime = today.toLocalTime()
        return repository.getHoursWithAvailability(
            code = code,
            dayStart = selectedDay,
            dayEnd = endSelectedDay,
            currentDayStart = currentDayStart,
            endSevenDaysFromNow = endSevenDaysFromNow
        ).map { list->
            if (!isToday) list
            else list.map {h ->
                val hourTime = LocalTime.parse(h.hour)
                if (hourTime.isBefore(nowTime)) h.copy(isFree = false)
                else h
            }
        }
    }
}

