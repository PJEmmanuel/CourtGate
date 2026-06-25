package com.example.courtgate.ui.presentation.booking

import app.cash.turbine.test
import com.example.courtgate.ResultCourt
import com.example.courtgate.ResultManage
import com.example.courtgate.core.CoroutinesTestRule
import com.example.courtgate.createCourtTest
import com.example.courtgate.createFreeHoursOfCourt
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.usecases.booking.GetCourtSelectedByCodeUseCase
import com.example.courtgate.usecases.booking.GetFreeHoursOnReservedCourtsUseCase
import com.example.courtgate.usecases.booking.SetNewBookingUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class BookingViewModelTest {
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var getCourtSelectedByCodeUseCase: GetCourtSelectedByCodeUseCase

    @Mock
    lateinit var getFreeHoursOnReservedCourtsUseCase: GetFreeHoursOnReservedCourtsUseCase

    @Mock
    lateinit var setNewBookingUseCase: SetNewBookingUseCase

    private lateinit var vm: BookingViewModel

    private val code = "pista_central"
    private val selectedDayMillis = 1736506800000L
    private val selectedHour = "10:00"
    private val expectedCourt = createCourtTest(1, "indoor")
    private val freeHourList = listOf(
        createFreeHoursOfCourt("10:00", true),
        createFreeHoursOfCourt("11:30", true)
    )

    private fun stubHappyPathFlows() {
        whenever(getCourtSelectedByCodeUseCase.invoke(any()))
            .thenReturn(flowOf(expectedCourt))
        whenever(getFreeHoursOnReservedCourtsUseCase.invoke(any(), any()))
            .thenReturn(flowOf(freeHourList))
    }

    private fun buildVm() = BookingViewModel(
        getCourtSelectedByCodeUseCase = getCourtSelectedByCodeUseCase,
        getFreeHoursOnReservedCourtsUseCase = getFreeHoursOnReservedCourtsUseCase,
        setNewBookingUseCase = setNewBookingUseCase,
        code = code,
        selectedDay = selectedDayMillis
    )

    @Test
    fun `onConfirmBooking transitions to Succeeded when use case returns success`(): Unit =
        runTest {
            stubHappyPathFlows()

            whenever(setNewBookingUseCase.invoke(any(), any(), any()))
                .doReturn(ResultManage.Success(Unit))

            vm = buildVm()

            vm.state.test {
                assertEquals(ResultCourt.Loading, awaitItem())
                val success = awaitItem() as ResultCourt.Success
                assertEquals(expectedCourt, success.data.requestedCourt)
                assertEquals(freeHourList, success.data.freeHoursOfCourt)

                vm.onSelectHour(selectedHour)
                vm.onBookClicked()
                vm.onConfirmBooking()

                runCurrent()

                val finalState = expectMostRecentItem() as ResultCourt.Success

                assertEquals(NewBookingFlowState.Succeeded, finalState.data.newBookingFlowState)
                verify(setNewBookingUseCase, times(1)).invoke(code, selectedDayMillis, selectedHour)

                cancelAndIgnoreRemainingEvents()

            }
        }

    @Test
    fun `onConfirmBooking transitions to Failed with the use case error when it fails`(): Unit =
        runTest {
            val expectedError = DomainError.Booking.SlotInPast
            stubHappyPathFlows()

            whenever(setNewBookingUseCase.invoke(any(), any(), any()))
                .doReturn(ResultManage.Failure(expectedError))

            vm = buildVm()

            vm.state.test {
                assertEquals(ResultCourt.Loading, awaitItem())
                val success = awaitItem() as ResultCourt.Success
                assertEquals(expectedCourt, success.data.requestedCourt)
                assertEquals(freeHourList, success.data.freeHoursOfCourt)

                vm.onSelectHour(selectedHour)
                vm.onBookClicked()
                vm.onConfirmBooking()

                runCurrent()

                val finalState = expectMostRecentItem() as ResultCourt.Success
                val sheet = finalState.data.newBookingFlowState

                assertEquals(true, sheet is NewBookingFlowState.Failed)
                assertEquals(expectedError, (sheet as NewBookingFlowState.Failed).error)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onConfirmBooking is ignored when sheet is already Submitting`(): Unit =
        runTest {
            stubHappyPathFlows()

            whenever(setNewBookingUseCase.invoke(any(), any(), any()))
                .doSuspendableAnswer { awaitCancellation() }

            vm = buildVm()

            vm.state.test {
                assertEquals(ResultCourt.Loading, awaitItem())
                val success = awaitItem() as ResultCourt.Success
                assertEquals(expectedCourt, success.data.requestedCourt)
                assertEquals(freeHourList, success.data.freeHoursOfCourt)

                vm.onSelectHour(selectedHour)
                vm.onBookClicked()
                vm.onConfirmBooking()
                //Segunda llamada
                vm.onConfirmBooking()

                runCurrent()

                verify(setNewBookingUseCase, times(1))
                    .invoke(code, selectedDayMillis, selectedHour)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onDismissSheet is ignored when sheet is Submitting`(): Unit =
        runTest {
            stubHappyPathFlows()
            whenever(setNewBookingUseCase.invoke(any(), any(), any()))
                .doSuspendableAnswer { awaitCancellation() }

            vm = buildVm()

            vm.state.test {
                assertEquals(ResultCourt.Loading, awaitItem())
                val success = awaitItem() as ResultCourt.Success
                assertEquals(expectedCourt, success.data.requestedCourt)
                assertEquals(freeHourList, success.data.freeHoursOfCourt)

                vm.onSelectHour(selectedHour)
                vm.onBookClicked()
                vm.onConfirmBooking()
                vm.onDismissSheet()


                runCurrent()

                val state = expectMostRecentItem() as ResultCourt.Success
                assertEquals(NewBookingFlowState.Submitting, state.data.newBookingFlowState)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `isSelectedHourStillFree becomes false when selected hour becomes unavailable`(): Unit =
        runTest {
            val updatedFreeHourList = listOf(
                createFreeHoursOfCourt("10:00", false),
                createFreeHoursOfCourt("11:30", true)
            )
            val freeHoursFlow = MutableStateFlow(freeHourList)

            whenever(getCourtSelectedByCodeUseCase.invoke(any()))
                .thenReturn(flowOf(expectedCourt))
            whenever(getFreeHoursOnReservedCourtsUseCase.invoke(any(), any()))
                .thenReturn(freeHoursFlow)

            vm = buildVm()

            vm.state.test {
                assertEquals(ResultCourt.Loading, awaitItem())

                val initial = awaitItem() as ResultCourt.Success
                assertEquals(expectedCourt, initial.data.requestedCourt)
                assertEquals(freeHourList, initial.data.freeHoursOfCourt)

                vm.onSelectHour(selectedHour)
                val afterSelect = awaitItem() as ResultCourt.Success
                assertEquals(selectedHour, afterSelect.data.selectedHourToBook)
                assertEquals(true, afterSelect.data.isSelectedHourStillFree)

                freeHoursFlow.value = updatedFreeHourList
                val afterUpdate = awaitItem() as ResultCourt.Success
                assertEquals(updatedFreeHourList, afterUpdate.data.freeHoursOfCourt)
                assertEquals(false, afterUpdate.data.isSelectedHourStillFree)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `state emits Error when an upstream flow throws DomainException`(): Unit =
        runTest {
            val expectedError = DomainError.Remote.UnknownRemoteError

            whenever(getCourtSelectedByCodeUseCase.invoke(any()))
                .thenReturn(flowOf(expectedCourt))
            whenever(getFreeHoursOnReservedCourtsUseCase.invoke(any(), any()))
                .thenReturn(flow { throw DomainException(DomainError.Remote.UnknownRemoteError) })

            vm = BookingViewModel(
                getCourtSelectedByCodeUseCase = getCourtSelectedByCodeUseCase,
                getFreeHoursOnReservedCourtsUseCase = getFreeHoursOnReservedCourtsUseCase,
                setNewBookingUseCase = setNewBookingUseCase,
                code = code,
                selectedDay = selectedDayMillis
            )

            vm.state.test {
                assertEquals(ResultCourt.Loading, awaitItem())
                val error = awaitItem() as ResultCourt.Error
                assertEquals(expectedError, error.error)

                cancelAndIgnoreRemainingEvents()
            }
        }
}