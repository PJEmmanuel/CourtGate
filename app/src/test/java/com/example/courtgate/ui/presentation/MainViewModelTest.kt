package com.example.courtgate.ui.presentation

import app.cash.turbine.test
import com.example.courtgate.core.CoroutinesTestRule
import com.example.courtgate.usecases.authentication.IsUserLoggedInUseCase
import com.example.courtgate.usecases.authentication.ObserveAuthStateUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var isUserLoggedInUseCase: IsUserLoggedInUseCase

    @Mock
    lateinit var observeAuthStateUseCase: ObserveAuthStateUseCase

    private lateinit var vm: MainViewModel

    @Before
    fun setUp() {
        whenever(isUserLoggedInUseCase()).thenReturn(false)
        whenever(observeAuthStateUseCase()).thenReturn(flow { emit(false) })
        vm = MainViewModel(isUserLoggedInUseCase, observeAuthStateUseCase)
    }

    @Test
    fun `initialValue is true when user is already logged in`() {
        whenever(isUserLoggedInUseCase()).thenReturn(true)
        whenever(observeAuthStateUseCase()).thenReturn(flow { emit(true) })
        val vm = MainViewModel(isUserLoggedInUseCase, observeAuthStateUseCase)

        assertEquals(true, vm.isLoggedIn.value)
    }

    @Test
    fun `initialValue is false when user is not logged in`() {
        assertEquals(false, vm.isLoggedIn.value)
    }

    @Test
    fun `isLoggedIn emits true when observeAuthState emits true`(): Unit = runTest {
        whenever(isUserLoggedInUseCase()).thenReturn(false)
        whenever(observeAuthStateUseCase()).thenReturn(flow { emit(true) })
        val vm = MainViewModel(isUserLoggedInUseCase, observeAuthStateUseCase)

        vm.isLoggedIn.test {
            assertEquals(false, awaitItem())
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `isLoggedIn emits false when observeAuthState emits false`(): Unit = runTest {
        whenever(isUserLoggedInUseCase()).thenReturn(true)
        whenever(observeAuthStateUseCase()).thenReturn(flow { emit(false) })
        val vm = MainViewModel(isUserLoggedInUseCase, observeAuthStateUseCase)

        vm.isLoggedIn.test {
            assertEquals(true, awaitItem())
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
