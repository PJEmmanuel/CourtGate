package com.example.courtgate.data

import com.example.courtgate.ResultManage
import com.example.courtgate.core.CoroutinesTestRule
import com.example.courtgate.createCourtBookingTest
import com.example.courtgate.createCourtTest
import com.example.courtgate.data.datasources.CourtLocalDataSource
import com.example.courtgate.data.datasources.CourtRemoteDataSource
import com.example.courtgate.data.datasources.SyncPreferencesDataSource
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant
import java.time.LocalDate

@RunWith(MockitoJUnitRunner::class)
class ManageCourtRepositoryTest {

    private val currentDayStart = Instant.parse("2025-05-19T22:00:00Z")
    private val endSevenDaysFromNow = Instant.parse("2025-05-26T22:00:00Z")

    @Mock
    lateinit var remoteDS: CourtRemoteDataSource

    @Mock
    lateinit var localDS: CourtLocalDataSource

    @Mock
    lateinit var syncPreferencesDS: SyncPreferencesDataSource

    @get:Rule
    val ioDispatcher = CoroutinesTestRule()

    private lateinit var repository: ManageCourtRepository

    @Before
    fun setUp() {
        repository = ManageCourtRepository(
            remoteDataSource = remoteDS,
            localDataSource = localDS,
            syncPreferencesDataSource = syncPreferencesDS,
            ioDispatcher = ioDispatcher.testDispatcher,
        )
    }

    @Test
    fun `when sync succeeds then emits courts from local`(): Unit = runTest {
        val expectedCourtList = listOf(createCourtTest(1, "indoor"), createCourtTest(2, "outdoor"))

        whenever(syncPreferencesDS.getLastSyncDay()).thenReturn(LocalDate.now().toString())
        whenever(localDS.getCourtsCount()).thenReturn(3)
        whenever(localDS.getAvailableCourts(anyOrNull(), any(), any()))
            .thenReturn(flowOf(expectedCourtList))
        whenever(remoteDS.getBookingsSevenDaysAhead(any(), any()))
            .thenReturn(flowOf(emptyList()))

        val result = repository.getAllCourtToShow(
            null,
            0L,
            0L,
            currentDayStart = currentDayStart,
            endSevenDaysFromNow = endSevenDaysFromNow,
        ).first()

        assertEquals(expectedCourtList, result)
    }

    @Test(expected = DomainException::class)
    fun `when sync fails then throws DomainException`(): Unit = runTest {
        whenever(syncPreferencesDS.getLastSyncDay()).thenReturn(LocalDate.now().toString())
        whenever(localDS.getCourtsCount()).thenReturn(0)
        whenever(localDS.getScheduleCount()).thenReturn(0)
        whenever(remoteDS.getAllCourt()).thenReturn(ResultManage.Failure(DomainError.Remote.NotFound))

        repository.getAllCourtToShow(
            null,
            0L,
            0L,
            currentDayStart = currentDayStart,
            endSevenDaysFromNow = endSevenDaysFromNow,
        ).first()
    }

    @Test
    fun `bookings from remote are synced to local`(): Unit = runTest {
        val expectedBooking = listOf(createCourtBookingTest(1), createCourtBookingTest(2))

        whenever(syncPreferencesDS.getLastSyncDay()).thenReturn(LocalDate.now().toString())
        whenever(localDS.getCourtsCount()).thenReturn(3)
        whenever(
            remoteDS.getBookingsSevenDaysAhead(any(), any())
        ).thenReturn(flowOf(expectedBooking))
        whenever(localDS.getAvailableCourts(anyOrNull(), any(), any()))
            .thenReturn(flowOf(emptyList()))

        repository.getAllCourtToShow(
            null,
            0L,
            0L,
            currentDayStart = currentDayStart,
            endSevenDaysFromNow = endSevenDaysFromNow,
        ).first()

        verify(localDS).syncBookings(
            windowStart = currentDayStart.toEpochMilli(),
            windowEnd = endSevenDaysFromNow.toEpochMilli(),
            bookings = expectedBooking
        )
    }
}