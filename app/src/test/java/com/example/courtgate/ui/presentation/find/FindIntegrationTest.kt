package com.example.courtgate.ui.presentation.find

import app.cash.turbine.test
import com.example.courtgate.ResultCourt
import com.example.courtgate.buildManageCourtRepository
import com.example.courtgate.core.CoroutinesTestRule
import com.example.courtgate.createCourtTest
import com.example.courtgate.createFilterOptionsTest
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.FilterOption
import com.example.courtgate.usecases.find.GetAllCourtToShowUseCase
import com.example.courtgate.usecases.find.GetFilterOptionUseCase
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.time.ExperimentalTime

class FindIntegrationTest {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    private val fixedInstant = Instant.parse("2025-04-10T10:00:00Z")
    private val todayClock = Clock.fixed(fixedInstant, ZoneOffset.UTC)
    private val localDS = listOf(
        createCourtTest(1, "indoor"),
        createCourtTest(2, "outdoor")
    )
    private val remoteDS = listOf(
        createCourtTest(1, "indoor"),
        createCourtTest(2, "outdoor")
    )
    private val schedule = listOf(
        "08:00",
        "09:30",
        "11:00",
        "12:30",
        "16:00",
        "17:30",
        "19:00",
        "20:30",
        "22:00"
    )
    private val syncDay = todayClock.toString()

    @Test
    fun `when local is empty, call remote DB and save static data`(): Unit = runTest {

        val vm = buildFindVM(
            clock = todayClock,
            schedule = schedule,
            localDS = localDS,
            remoteDS = remoteDS,
            syncPreferenceDS = syncDay,
            ioDispatcher = coroutinesTestRule.testDispatcher
        )

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            val success = awaitItem() as ResultCourt.Success
            assertEquals(localDS, success.data.courts)
        }
    }

    @Test
    fun `when local is empty, call remote DB but not found then get remote error`(): Unit =
        runTest {

            val vm = buildFindVM(
                clock = todayClock,
                remoteError = DomainError.Remote.NotFound,
                syncPreferenceDS = syncDay,
                ioDispatcher = coroutinesTestRule.testDispatcher
            )

            vm.state.test {
                assertEquals(ResultCourt.Loading, awaitItem())
                assertEquals(ResultCourt.Error(DomainError.Remote.NotFound), awaitItem())
            }
        }

    @Test
    fun `when local not empty, get distinct filter from court`(): Unit = runTest {
        val expectedFilter = listOf(
            createFilterOptionsTest("indoor", false),
            createFilterOptionsTest("outdoor", false)
        )

        val vm = buildFindVM(
            clock = todayClock,
            schedule = schedule,
            localDS = localDS,
            remoteDS = remoteDS,
            syncPreferenceDS = syncDay,
            ioDispatcher = coroutinesTestRule.testDispatcher
        )

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            val success = awaitItem() as ResultCourt.Success
            assertEquals(expectedFilter, success.data.filterList)
        }
    }

    @Test
    fun `when local not empty, get filtered court`(): Unit = runTest {
        val filter = "outdoor"
        val expectedCourt = listOf(createCourtTest(2, "outdoor"))
        val expectedFilter = listOf(
            FilterOption(
                located = "indoor",
                isSelected = false
            ), FilterOption(
                located = "outdoor",
                isSelected = true
            )
        )

        val vm = buildFindVM(
            clock = todayClock,
            schedule = schedule,
            localDS = localDS,
            remoteDS = remoteDS,
            syncPreferenceDS = syncDay,
            ioDispatcher = coroutinesTestRule.testDispatcher
        )

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            skipItems(1)
            vm.onFilter(filter)
            val success = awaitItem() as ResultCourt.Success
            assertEquals(expectedCourt, success.data.courts)
            assertEquals(expectedFilter, success.data.filterList)
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun buildFindVM(
    clock: Clock,
    bookings: List<CourtBooking> = emptyList(),
    schedule: List<String> = emptyList(),
    remoteDS: List<Court> = emptyList(),
    localDS: List<Court> = emptyList(),
    remoteError: DomainError? = null,
    syncPreferenceDS: String = "",
    ioDispatcher: TestDispatcher
): FindViewModel {
    val repository = buildManageCourtRepository(
        bookings = bookings,
        schedule = schedule,
        remoteDS = remoteDS,
        localDS = localDS,
        remoteError = remoteError,
        syncPreferenceDS = syncPreferenceDS,
        ioDispatcher = ioDispatcher
    )
    return FindViewModel(
        getAllCourtToShowUseCase = GetAllCourtToShowUseCase(
            repository = repository,
            clock = clock
        ),
        getFilterOptionUseCase = GetFilterOptionUseCase(
            repository = repository
        )
    )
}
