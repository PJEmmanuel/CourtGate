package com.example.courtgate.ui.presentation.find

import app.cash.turbine.test
import com.example.courtgate.ResultCourt
import com.example.courtgate.core.CoroutinesTestRule
import com.example.courtgate.createCourtTest
import com.example.courtgate.createFilterOptionsTest
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.domain.models.FilterOption
import com.example.courtgate.usecases.find.GetAllCourtToShowUseCase
import com.example.courtgate.usecases.find.GetFilterOptionUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import java.time.ZonedDateTime

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class FindViewModelTest {
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var getAllCourtToShowUseCase: GetAllCourtToShowUseCase

    @Mock
    lateinit var getFilterOptionUseCase: GetFilterOptionUseCase

    private lateinit var vm: FindViewModel

    private val defaultCourtList = listOf(
        createCourtTest(1, "indoor"),
        createCourtTest(2, "outdoor")
    )

    private val defaultFilterList = listOf(
        createFilterOptionsTest("indoor", false),
        createFilterOptionsTest("outdoor", false)
    )

    private fun createViewModel(
        courts: List<Court> = defaultCourtList,
        filters: List<FilterOption> = defaultFilterList
    ) {
        whenever(getAllCourtToShowUseCase.invoke(anyOrNull(), any())).thenReturn(flowOf(courts))
        whenever(getFilterOptionUseCase.invoke()).thenReturn(flowOf(filters))
        vm = FindViewModel(getAllCourtToShowUseCase, getFilterOptionUseCase)
    }

    @Test
    fun `state starts as Loading`(): Unit = runTest {
        whenever(getFilterOptionUseCase.invoke()).thenReturn(flowOf(defaultFilterList))
        vm = FindViewModel(getAllCourtToShowUseCase, getFilterOptionUseCase)

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
        }
    }

    @Test
    fun `emits Success with courts when use case returns data`(): Unit = runTest {
        createViewModel()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            val success = awaitItem() as ResultCourt.Success
            assertEquals(defaultCourtList, success.data.courts)
            assertEquals(defaultFilterList, success.data.filterList)
        }
    }

    @Test
    fun `emits Success with empty filter options results in empty filterList`(): Unit = runTest {
        createViewModel(filters = emptyList())

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            val success = awaitItem() as ResultCourt.Success
            assertEquals(defaultCourtList, success.data.courts)
            assertEquals(emptyList<FilterOption>(), success.data.filterList)
        }
    }

    @Test
    fun `selecting a filter marks only that filter as selected`(): Unit = runTest {
        val expectedFilter = listOf(
            createFilterOptionsTest("indoor", true),
            createFilterOptionsTest("outdoor", false)
        )
        createViewModel()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            awaitItem() as ResultCourt.Success

            vm.onFilter("indoor")
            val filterResult = awaitItem() as ResultCourt.Success

            assertEquals(expectedFilter, filterResult.data.filterList)
        }
    }

    @Test
    fun `selecting same filter again deselects it`(): Unit = runTest {
        val expectedFilterAfterFirstSelect = listOf(
            createFilterOptionsTest("indoor", true),
            createFilterOptionsTest("outdoor", false)
        )
        val expectedFilterAfterDeselect = listOf(
            createFilterOptionsTest("indoor", false),
            createFilterOptionsTest("outdoor", false)
        )
        createViewModel()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            awaitItem() as ResultCourt.Success

            vm.onFilter("indoor")
            val indoorTrue = awaitItem() as ResultCourt.Success

            vm.onFilter("indoor")
            val indoorFalse = awaitItem() as ResultCourt.Success

            assertEquals(expectedFilterAfterFirstSelect, indoorTrue.data.filterList)
            assertEquals(expectedFilterAfterDeselect, indoorFalse.data.filterList)
        }
    }

    @Test
    fun `changing date updates selectedDate in state`(): Unit = runTest {
        val expectedDate = ZonedDateTime.parse("2025-10-15T10:00:00Z")
        createViewModel()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            awaitItem() as ResultCourt.Success

            vm.onSelectedDate(expectedDate)
            val resultDate = awaitItem() as ResultCourt.Success

            assertEquals(expectedDate, resultDate.data.selectedDate)
        }
    }

    @Test
    fun `DomainException maps to its specific error`(): Unit = runTest {
        whenever(getAllCourtToShowUseCase.invoke(anyOrNull(), any())).thenReturn(
            flow { throw DomainException(DomainError.Remote.NotFound) }
        )
        whenever(getFilterOptionUseCase.invoke()).thenReturn(flowOf(emptyList()))
        vm = FindViewModel(getAllCourtToShowUseCase, getFilterOptionUseCase)

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            val resultError = awaitItem() as ResultCourt.Error
            assertEquals(DomainError.Remote.NotFound, resultError.error)
        }
    }

    @Test
    fun `unknown exception maps to UnknownRemoteError`(): Unit = runTest {
        whenever(getAllCourtToShowUseCase.invoke(anyOrNull(), any())).thenReturn(
            flow { throw RuntimeException("Testando errores") }
        )
        whenever(getFilterOptionUseCase.invoke()).thenReturn(flowOf(emptyList()))
        vm = FindViewModel(getAllCourtToShowUseCase, getFilterOptionUseCase)

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            val resultError = awaitItem() as ResultCourt.Error
            assertEquals(DomainError.Remote.UnknownRemoteError, resultError.error)
        }
    }

    @Test
    fun `onRetry after error triggers new fetch and can succeed`(): Unit = runTest {
        val expectedDate = ZonedDateTime.parse("2025-10-15T10:00:00Z")
        val expectedError = DomainException(DomainError.Remote.NotFound)

        whenever(getAllCourtToShowUseCase.invoke(anyOrNull(), any()))
            .thenReturn(flow { throw expectedError })
            .thenReturn(flowOf(defaultCourtList))
        whenever(getFilterOptionUseCase.invoke()).thenReturn(flowOf(emptyList()))
        vm = FindViewModel(getAllCourtToShowUseCase, getFilterOptionUseCase)

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            val resultError = awaitItem() as ResultCourt.Error

            vm.onRetry(expectedDate)
            val resultCourt = awaitItem() as ResultCourt.Success

            assertEquals(expectedError.error, resultError.error)
            assertEquals(defaultCourtList, resultCourt.data.courts)
        }
    }
}
