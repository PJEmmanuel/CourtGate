package com.example.courtgate.usecases.booking

import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.domain.models.FreeHoursOfCourt
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class GetFreeHoursOnReservedCourtsUseCaseTest {

    private val zone = ZoneOffset.UTC
    private val clock = Clock.fixed(Instant.parse("2025-01-10T12:00:00Z"), zone)

    private val listTest = listOf(
        FreeHoursOfCourt("08:00", true),
        FreeHoursOfCourt("10:00", true),
        FreeHoursOfCourt("12:00", true)
    )
    private val code = "pista_central"

    private lateinit var manageRepo: ManageCourtRepository
    private lateinit var useCase: GetFreeHoursOnReservedCourtsUseCase


    @Before
    fun setUp() {
        manageRepo = mock()
        useCase = GetFreeHoursOnReservedCourtsUseCase(
            repository = manageRepo,
            clock = clock
        )
    }

    @Test
    fun `invoke marks past hours as not free when selected day is today`(): Unit = runTest {
        val expectedListTest = listOf(
            FreeHoursOfCourt("08:00", false),
            FreeHoursOfCourt("10:00", false),
            FreeHoursOfCourt("12:00", true)
        )

        val pastHourMillis = "2025-01-10T11:00:00Z"
        val selectedDay = Instant.parse(pastHourMillis).toEpochMilli()

        whenever(manageRepo.getHoursWithAvailability(any(), any(), any(), any(), any()))
            .doReturn(flowOf(listTest))

        val result = useCase.invoke(code, selectedDay).first()

        assertEquals(expectedListTest, result)
    }

    @Test
    fun `invoke returns list unchanged when selected day is not today`(): Unit = runTest {
        val futureDayMillis = "2025-01-15T11:00:00Z"
        val selectedDay = Instant.parse(futureDayMillis).toEpochMilli()

        whenever(manageRepo.getHoursWithAvailability(any(), any(), any(), any(), any()))
            .doReturn(flowOf(listTest))

        val result = useCase.invoke(code, selectedDay).first()

        assertEquals(listTest, result)
    }

    @Test
    fun `invoke keeps hour as free when it equals current time`(): Unit = runTest {
        val expectedListTest = listOf(
            FreeHoursOfCourt("08:00", false),
            FreeHoursOfCourt("10:00", false),
            FreeHoursOfCourt("12:00", true)
        )

        val justNowMillis = "2025-01-10T12:00:00Z"
        val selectedDay = Instant.parse(justNowMillis).toEpochMilli()

        whenever(manageRepo.getHoursWithAvailability(any(), any(), any(), any(), any()))
            .doReturn(flowOf(listTest))

        val result = useCase.invoke(code, selectedDay).first()

        assertEquals(expectedListTest, result)
    }
}