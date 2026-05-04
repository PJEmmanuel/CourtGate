package com.example.courtgate.ui.presentation.home

import app.cash.turbine.test
import com.example.courtgate.ResultCourt
import com.example.courtgate.core.CoroutinesTestRule
import com.example.courtgate.core.lastResultTest
import com.example.courtgate.domain.models.LastResult
import com.example.courtgate.usecases.home.GetLastResultUseCase
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var getLastResultUseCase: GetLastResultUseCase

    private lateinit var vm: HomeViewModel

    private val lastResult1 = lastResultTest(5, 6, 0, 1, 6, 6, 4)


    @Test
    fun `If get lastResult then show Success`(): Unit = runTest {
        val listLastResult = listOf(lastResult1)
        whenever(getLastResultUseCase.invoke()).thenReturn(flowOf(listLastResult))
        vm = HomeViewModel(getLastResultUseCase)

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Success(listLastResult), awaitItem())
        }
    }

    @Test
    fun `If lastResult updates, show updated Success`(): Unit = runTest {
        val firstList  = listOf(lastResult1)
        val secondList = listOf(lastResultTest(1, 3, 6, 6, 2, 0, 0))

        whenever(getLastResultUseCase.invoke()).thenReturn(
            flow {
                emit(firstList)
                emit(secondList)
            }
        )
        vm = HomeViewModel(getLastResultUseCase)

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Success(firstList), awaitItem())
            assertEquals(ResultCourt.Success(secondList), awaitItem())
        }
    }

    @Test
    fun `If get empty lastResult then show Success`(): Unit = runTest {
        val emptyListLastResult = emptyList<LastResult>()
        whenever(getLastResultUseCase.invoke()).thenReturn(flowOf(emptyListLastResult))
        vm = HomeViewModel(getLastResultUseCase)

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Success(emptyListLastResult), awaitItem())
        }
    }

    @Test
    fun `If get any errors then show Error`(): Unit = runTest {
        val error = RuntimeException("Fallo en Room")

        whenever(getLastResultUseCase.invoke()).thenReturn(flow { throw error })
        vm = HomeViewModel(getLastResultUseCase)

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())

            // Ahora ResultCourt.Error contiene DomainError tipado, no Throwable
            val emittedError = awaitItem() as ResultCourt.Error
            assertEquals(com.example.courtgate.domain.models.DomainError.Local.UnknownLocalError, emittedError.error)
        }
    }
}