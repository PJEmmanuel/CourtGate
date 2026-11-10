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
import org.mockito.kotlin.nullableArgumentCaptor
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

        whenever(repository.getAllCourtToShow(anyOrNull(), any(), any(), any(), any(), anyOrNull()))
            .thenReturn(flowOf(emptyList()))

        useCase(null, dateWithTime).first()

        val captor = argumentCaptor<Long>()
        verify(repository).getAllCourtToShow(
            located = isNull(),
            selectedDay = captor.capture(),
            endSelectedDay = captor.capture(),
            currentDayStart = any(),
            endSevenDaysFromNow = any(),
            minHour = anyOrNull()
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

        whenever(repository.getAllCourtToShow(anyOrNull(), any(), any(), any(), any(), anyOrNull()))
            .thenReturn(flowOf(emptyList()))

        useCase(null, selectedDate).first()

        val startCaptor = argumentCaptor<Instant>()
        val endCaptor = argumentCaptor<Instant>()
        verify(repository).getAllCourtToShow(
            located = isNull(),
            selectedDay = any(),
            endSelectedDay = any(),
            currentDayStart = startCaptor.capture(),
            endSevenDaysFromNow = endCaptor.capture(),
            minHour = anyOrNull()
        )
        assertEquals(expectedCurrentDayStart, startCaptor.firstValue)
        assertEquals(expectedEndSevenDays, endCaptor.firstValue)
    }

    @Test
    fun `minHour is current hour when selected day is today on exact minute`() = runTest {
        assertEquals("12:00", captureMinHour(now = "2025-01-10T12:00:00Z", selectedDay = 10))
    }

    @Test
    fun `minHour rounds up to next minute when seconds have passed`() = runTest {
        assertEquals("12:01", captureMinHour(now = "2025-01-10T12:00:30Z", selectedDay = 10))
    }

    @Test
    fun `minHour is end of day when last minute of today has started`() = runTest {
        assertEquals("24:00", captureMinHour(now = "2025-01-10T23:59:30Z", selectedDay = 10))
    }

    @Test
    fun `minHour is null when selected day is not today`() = runTest {
        assertEquals(null, captureMinHour(now = "2025-01-10T12:00:30Z", selectedDay = 11))
    }

    private suspend fun captureMinHour(now: String, selectedDay: Int): String? {
        val repository: ManageCourtRepository = mock()
        val zone = ZoneOffset.UTC
        val clock = Clock.fixed(Instant.parse(now), zone)
        val useCase = GetAllCourtToShowUseCase(repository, clock)

        whenever(repository.getAllCourtToShow(anyOrNull(), any(), any(), any(), any(), anyOrNull()))
            .thenReturn(flowOf(emptyList()))

        useCase(null, ZonedDateTime.of(2025, 1, selectedDay, 9, 0, 0, 0, zone)).first()

        val minHourCaptor = nullableArgumentCaptor<String>()
        verify(repository).getAllCourtToShow(
            located = isNull(),
            selectedDay = any(),
            endSelectedDay = any(),
            currentDayStart = any(),
            endSevenDaysFromNow = any(),
            minHour = minHourCaptor.capture()
        )
        return minHourCaptor.firstValue
    }

}