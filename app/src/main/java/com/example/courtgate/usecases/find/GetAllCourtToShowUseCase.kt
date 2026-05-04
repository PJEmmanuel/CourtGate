package com.example.courtgate.usecases.find

import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.domain.models.Court
import kotlinx.coroutines.flow.Flow
import java.time.Clock
import java.time.ZonedDateTime
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

        return repository.getAllCourtToShow(
            located = located,
            selectedDay = selectedDay,
            endSelectedDay = endSelectedDay,
            currentDayStart = currentDayStart,
            endSevenDaysFromNow = endSevenDaysFromNow
        )
    }
}
