package com.example.courtgate.usecases.booking

import com.example.courtgate.ResultManage
import com.example.courtgate.data.AuthenticationRepository
import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.NewCourtBooking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Clock
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneOffset

class SetNewBookingUseCaseTest {

    private val zone = ZoneOffset.UTC
    private val instantFixed = Instant.parse("2025-01-10T12:00:00Z")
    private val clock = Clock.fixed(instantFixed, zone)

    private val code = "pista_central"
    private val todayMillis = instantFixed.toEpochMilli()

    private lateinit var manageRepo: ManageCourtRepository
    private lateinit var authRepo: AuthenticationRepository
    private lateinit var useCase: SetNewBookingUseCase

    @Before
    fun setUp() {
        manageRepo = mock()
        authRepo = mock {
            on { this.getCurrentUserId() } doReturn "userTest"
        }
        useCase = SetNewBookingUseCase(
            managerCourtRepository = manageRepo,
            authRepository = authRepo,
            clock = clock
        )
    }

    private fun expectedStartsAt(todayMillis: Long, hour: String): Instant =
        Instant.ofEpochMilli(todayMillis)
            .atZone(zone)
            .toLocalDate()
            .atTime(LocalTime.parse(hour))
            .atZone(zone)
            .toInstant()

    @Test
    fun `invoke returns UserNotFound when no current user`(): Unit = runTest {
        val hour = "15:00"

        whenever(authRepo.getCurrentUserId()).doReturn(null)
        val result = useCase.invoke(code, todayMillis, hour)

        verify(manageRepo, never()).setBooking(any())
        assertEquals(
            ResultManage.Failure(DomainError.Auth.UserNotFound),
            result
        )
    }

    @Test
    fun `invoke returns SlotInPast when slot is before now`(): Unit = runTest {
        val hour = "10:00"

        val result = useCase.invoke(code, todayMillis, hour)

        verify(manageRepo, never()).setBooking(any())
        assertEquals(
            ResultManage.Failure(DomainError.Booking.SlotInPast),
            result
        )
    }

    @Test
    fun `invoke delegates to repository when slot is in the future`(): Unit = runTest {
        val hour = "14:00"

        whenever(manageRepo.setBooking(any()))
            .doReturn(ResultManage.Success(Unit))

        val result = useCase.invoke(code, todayMillis, hour)
        val captor = argumentCaptor<NewCourtBooking>()

        verify(manageRepo, times(1)).setBooking(captor.capture())
        assertEquals(expectedStartsAt(todayMillis, hour), captor.firstValue.startsAt)
        assertEquals(ResultManage.Success(Unit), result)
    }

    @Test
    fun `invoke accepts slot when it is exactly now`(): Unit = runTest {
        val hour = "12:00"

        whenever(manageRepo.setBooking(any()))
            .doReturn(ResultManage.Success(Unit))

        val result = useCase.invoke(code, todayMillis, hour)
        val captor = argumentCaptor<NewCourtBooking>()

        verify(manageRepo, times(1)).setBooking(captor.capture())
        assertEquals(expectedStartsAt(todayMillis, hour), captor.firstValue.startsAt)
        assertEquals(ResultManage.Success(Unit), result)
    }
}