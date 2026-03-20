package com.example.courtgate.usecases.find

import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.domain.models.Court
import kotlinx.coroutines.flow.Flow
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class GetAllCourtToShowUseCase @Inject constructor(private val repository: ManageCourtRepository) {
    operator fun invoke(located: String?, date: ZonedDateTime): Flow<List<Court>> {

        //TODO: revisar ZoneId
        val defaultZone = ZoneId.of("Europe/Madrid")

        val selectedDay = date.toLocalDate().atStartOfDay(defaultZone).toInstant().toEpochMilli()
        val endSelectedDay =
            date.toLocalDate().plusDays(1).atStartOfDay(defaultZone).toInstant().toEpochMilli()

        val today = ZonedDateTime.now(defaultZone)
        val currentDayStart = today.toLocalDate().atStartOfDay(defaultZone).toInstant()
        val endSevenDaysFromNow =
            today.toLocalDate().plusDays(7).atTime(23, 59, 59).atZone(defaultZone).toInstant()

        return repository.getAllCourtToShow(
            located = located,
            selectedDay = selectedDay,
            endSelectedDay = endSelectedDay,
            currentDayStart = currentDayStart,
            endSevenDaysFromNow = endSevenDaysFromNow
        )
    }
}