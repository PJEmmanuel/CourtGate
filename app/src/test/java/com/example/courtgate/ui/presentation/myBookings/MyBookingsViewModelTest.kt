package com.example.courtgate.ui.presentation.myBookings

import app.cash.turbine.test
import com.example.courtgate.ResultCourt
import com.example.courtgate.ResultManage
import com.example.courtgate.core.CoroutinesTestRule
import com.example.courtgate.createCourtBookingTest
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.usecases.booking.DeleteMyBookingsUseCase
import com.example.courtgate.usecases.booking.GetMyBookingsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
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
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MyBookingsViewModelTest {
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var getMyBookingsUseCase: GetMyBookingsUseCase

    @Mock
    lateinit var deleteMyBookingsUseCase: DeleteMyBookingsUseCase

    private lateinit var vm: MyBookingsViewModel

    private val booking = createCourtBookingTest(1)
    private val myBookings = listOf(booking, createCourtBookingTest(2))

    private fun stubHappyPathFlow() {
        whenever(getMyBookingsUseCase.invoke()).thenReturn(flowOf(myBookings))
    }

    private fun buildVm() = MyBookingsViewModel(
        getMyBookingsUseCase = getMyBookingsUseCase,
        deleteMyBookingsUseCase = deleteMyBookingsUseCase
    )

    @Test
    fun `state emits Loading and then Success with the user bookings`(): Unit = runTest {
        stubHappyPathFlow()

        vm = buildVm()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            val success = awaitItem() as ResultCourt.Success
            assertEquals(myBookings, success.data.myBookingList)
            assertEquals(MyBookingFlowState.Hidden, success.data.myBookingFlowState)
        }
    }

    @Test
    fun `state emits Error with the domain error when bookings flow fails`(): Unit = runTest {
        val expectedError = DomainError.Auth.NetworkError
        whenever(getMyBookingsUseCase.invoke())
            .thenReturn(flow { throw DomainException(expectedError) })

        vm = buildVm()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Error(expectedError), awaitItem())
        }
    }

    @Test
    fun `onRetryLoad after error loads bookings again and can succeed`(): Unit = runTest {
        whenever(getMyBookingsUseCase.invoke())
            .thenReturn(flow { throw DomainException(DomainError.Remote.ServerError) })
            .thenReturn(flowOf(myBookings))

        vm = buildVm()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Error(DomainError.Remote.ServerError, 1), awaitItem())

            vm.onRetryLoad()
            assertEquals(ResultCourt.Loading, awaitItem())
            val success = awaitItem() as ResultCourt.Success
            assertEquals(myBookings, success.data.myBookingList)
        }
    }

    @Test
    fun `repeated failures increase failedAttempts`(): Unit = runTest {
        whenever(getMyBookingsUseCase.invoke())
            .thenReturn(flow { throw DomainException(DomainError.Remote.ServerError) })

        vm = buildVm()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Error(DomainError.Remote.ServerError, 1), awaitItem())

            vm.onRetryLoad()
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Error(DomainError.Remote.ServerError, 2), awaitItem())

            vm.onRetryLoad()
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Error(DomainError.Remote.ServerError, 3), awaitItem())
        }
    }

    @Test
    fun `onBookClicked opens the sheet in Confirming with the selected booking`(): Unit =
        runTest {
            stubHappyPathFlow()

            vm = buildVm()

            vm.state.test {
                assertEquals(ResultCourt.Loading, awaitItem())
                awaitItem()

                vm.onBookClicked(booking)
                runCurrent()

                val finalState = expectMostRecentItem() as ResultCourt.Success
                assertEquals(
                    MyBookingFlowState.Confirming(booking),
                    finalState.data.myBookingFlowState
                )
            }
        }

    @Test
    fun `onConfirmEditBook transitions to Succeeded when delete succeeds`(): Unit = runTest {
        stubHappyPathFlow()
        whenever(deleteMyBookingsUseCase.invoke(any())).doReturn(ResultManage.Success(Unit))

        vm = buildVm()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            awaitItem()

            vm.onBookClicked(booking)
            vm.onConfirmEditBook()
            runCurrent()

            val finalState = expectMostRecentItem() as ResultCourt.Success
            assertEquals(
                MyBookingFlowState.Succeeded(booking),
                finalState.data.myBookingFlowState
            )
            verify(deleteMyBookingsUseCase, times(1)).invoke(booking.id)
        }
    }

    @Test
    fun `onConfirmEditBook transitions to Failed with the use case error when delete fails`(): Unit =
        runTest {
            val expectedError = DomainError.Remote.ServerError
            stubHappyPathFlow()
            whenever(deleteMyBookingsUseCase.invoke(any()))
                .doReturn(ResultManage.Failure(expectedError))

            vm = buildVm()

            vm.state.test {
                assertEquals(ResultCourt.Loading, awaitItem())
                awaitItem()

                vm.onBookClicked(booking)
                vm.onConfirmEditBook()
                runCurrent()

                val finalState = expectMostRecentItem() as ResultCourt.Success
                assertEquals(
                    MyBookingFlowState.Failed(booking, expectedError),
                    finalState.data.myBookingFlowState
                )
            }
        }

    @Test
    fun `onRetryEdit calls delete again after a failure`(): Unit = runTest {
        stubHappyPathFlow()
        whenever(deleteMyBookingsUseCase.invoke(any()))
            .doReturn(ResultManage.Failure(DomainError.Remote.ServerError))
            .doReturn(ResultManage.Success(Unit))

        vm = buildVm()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            awaitItem()

            vm.onBookClicked(booking)
            vm.onConfirmEditBook()
            runCurrent()
            vm.onRetryEdit()
            runCurrent()

            val finalState = expectMostRecentItem() as ResultCourt.Success
            assertEquals(
                MyBookingFlowState.Succeeded(booking),
                finalState.data.myBookingFlowState
            )
            verify(deleteMyBookingsUseCase, times(2)).invoke(booking.id)
        }
    }

    @Test
    fun `onRetryEdit does nothing when the sheet is not in Failed`(): Unit = runTest {
        stubHappyPathFlow()

        vm = buildVm()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            awaitItem()

            vm.onBookClicked(booking)
            vm.onRetryEdit()
            runCurrent()

            val finalState = expectMostRecentItem() as ResultCourt.Success
            assertEquals(
                MyBookingFlowState.Confirming(booking),
                finalState.data.myBookingFlowState
            )
            verify(deleteMyBookingsUseCase, never()).invoke(any())
        }
    }

    @Test
    fun `onDismissSheet is ignored while Submitting`(): Unit = runTest {
        stubHappyPathFlow()
        whenever(deleteMyBookingsUseCase.invoke(any())).doSuspendableAnswer { awaitCancellation() }

        vm = buildVm()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            awaitItem()

            vm.onBookClicked(booking)
            vm.onConfirmEditBook()
            runCurrent()
            vm.onDismissSheet()
            runCurrent()

            val finalState = expectMostRecentItem() as ResultCourt.Success
            assertEquals(
                MyBookingFlowState.Submitting(booking),
                finalState.data.myBookingFlowState
            )
        }
    }

    @Test
    fun `onDismissSheet hides the sheet from Confirming`(): Unit = runTest {
        stubHappyPathFlow()

        vm = buildVm()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            awaitItem()

            vm.onBookClicked(booking)
            runCurrent()
            vm.onDismissSheet()
            runCurrent()

            val finalState = expectMostRecentItem() as ResultCourt.Success
            assertEquals(MyBookingFlowState.Hidden, finalState.data.myBookingFlowState)
        }
    }
}
