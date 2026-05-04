package com.example.courtgate.usecases.find

import com.example.courtgate.data.ManageCourtRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime

class GetAllCourtToShowUseCaseTest {

    @Test
    fun `selectedDay is start of day regardless of time component`(): Unit = runTest {
        val repository: ManageCourtRepository = mock()
        val zone = ZoneOffset.UTC
        val instantFixed = "2025-01-10T12:00:00Z"
        val clock = Clock.fixed(Instant.parse(instantFixed), zone)
        val useCase = GetAllCourtToShowUseCase(repository, clock)

        val dateWithTime = ZonedDateTime.of(2025, 1, 15, 14, 30, 0, 0, zone)
        val expectedDaySelectedDDay = LocalDate.of(2025, 1, 15)
            .atStartOfDay(zone)
            .toInstant().toEpochMilli()
        val expectedEndSelectedDay = LocalDate.of(2025, 1, 16)
            .atStartOfDay(zone).toInstant().toEpochMilli()

        whenever(repository.getAllCourtToShow(anyOrNull(), any(), any(), any(), any()))
            .thenReturn(flowOf(emptyList()))

        useCase(null, dateWithTime).first()

        val captor = argumentCaptor<Long>()
        verify(repository).getAllCourtToShow(
            located = isNull(),
            selectedDay = captor.capture(),
            endSelectedDay = captor.capture(),
            currentDayStart = any(),
            endSevenDaysFromNow = any()
        )

        assertEquals(expectedDaySelectedDDay, captor.firstValue)
        assertEquals(expectedEndSelectedDay, captor.secondValue)

    }

    @Test
    fun `seven day window is calculated from today not from selected date`() = runTest {
        val repository: ManageCourtRepository = mock()
        val zone = ZoneOffset.UTC
        val instantFixed = "2025-01-10T12:00:00Z"
        val clock = Clock.fixed(Instant.parse(instantFixed), zone)
        val useCase = GetAllCourtToShowUseCase(repository, clock)

        val selectedDate = ZonedDateTime.of(2025, 1, 15, 0, 0, 0, 0, zone)

        val expectedCurrentDayStart = LocalDate.of(2025, 1, 10)
            .atStartOfDay(zone).toInstant()
        val expectedEndSevenDays = LocalDate.of(2025, 1, 17)
            .atTime(23, 59, 59).atZone(zone).toInstant()

        whenever(repository.getAllCourtToShow(anyOrNull(), any(), any(), any(), any()))
            .thenReturn(flowOf(emptyList()))

        useCase(null, selectedDate).first()

        val startCaptor = argumentCaptor<Instant>()
        val endCaptor = argumentCaptor<Instant>()
        verify(repository).getAllCourtToShow(
            located = isNull(),
            selectedDay = any(),
            endSelectedDay = any(),
            currentDayStart = startCaptor.capture(),
            endSevenDaysFromNow = endCaptor.capture()
        )
        assertEquals(expectedCurrentDayStart, startCaptor.firstValue)
        assertEquals(expectedEndSevenDays, endCaptor.firstValue)
    }

}