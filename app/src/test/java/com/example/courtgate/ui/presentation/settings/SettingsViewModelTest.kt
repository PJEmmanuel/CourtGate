package com.example.courtgate.ui.presentation.settings

import app.cash.turbine.test
import com.example.courtgate.ResultCourt
import com.example.courtgate.ResultManage
import com.example.courtgate.core.CoroutinesTestRule
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.domain.models.User
import com.example.courtgate.usecases.settings.DeleteAccountUseCase
import com.example.courtgate.usecases.settings.GetCurrentUserUseCase
import com.example.courtgate.usecases.settings.SignOutUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SettingsViewModelTest {
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    @Mock
    lateinit var signOutUseCase: SignOutUseCase

    @Mock
    lateinit var deleteAccountUseCase: DeleteAccountUseCase

    private lateinit var vm: SettingsViewModel

    private val user = User(
        uid = "user_1",
        email = "asd@asd.com",
        createdAt = Instant.parse("2025-10-12T00:00:00Z")
    )
    private val password = "asdASD123"

    private fun stubUser() {
        whenever(getCurrentUserUseCase.invoke()).thenReturn(flowOf(user))
    }

    private fun buildVm() = SettingsViewModel(
        getCurrentUserUseCase = getCurrentUserUseCase,
        signOutUseCase = signOutUseCase,
        deleteAccountUseCase = deleteAccountUseCase
    )

    private fun ResultCourt<SettingsState>.dialog() = (this as ResultCourt.Success).data.dialog

    @Test
    fun `state emits Loading and then Success with the account data`(): Unit = runTest {
        stubUser()
        vm = buildVm()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Success(SettingsState(user = user)), awaitItem())
        }
    }

    @Test
    fun `state emits SessionExpired when there is no user`(): Unit = runTest {
        whenever(getCurrentUserUseCase.invoke())
            .thenReturn(flow { throw DomainException(DomainError.Auth.SessionExpired) })
        vm = buildVm()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Error(DomainError.Auth.SessionExpired), awaitItem())
        }
    }

    @Test
    fun `onRetry after error loads the account again`(): Unit = runTest {
        whenever(getCurrentUserUseCase.invoke())
            .thenReturn(flow { throw RuntimeException() })
            .thenReturn(flowOf(user))
        vm = buildVm()

        vm.state.test {
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Error(DomainError.Auth.UnknownAuthError, 1), awaitItem())

            vm.onRetry()
            assertEquals(ResultCourt.Loading, awaitItem())
            assertEquals(ResultCourt.Success(SettingsState(user = user)), awaitItem())
        }
    }

    @Test
    fun `onLogoutClicked asks for confirmation without signing out`(): Unit = runTest {
        stubUser()
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onLogoutClicked()
            runCurrent()

            assertEquals(SettingsDialogState.ConfirmingLogout, expectMostRecentItem().dialog())
            verify(signOutUseCase, never()).invoke()
            assertFalse(vm.sessionClosed.value)
        }
    }

    @Test
    fun `onConfirmLogout signs out and closes the session`(): Unit = runTest {
        stubUser()
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onLogoutClicked()
            vm.onConfirmLogout()
            runCurrent()

            verify(signOutUseCase).invoke()
            assertTrue(vm.sessionClosed.value)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onConfirmLogout does nothing without the confirmation dialog`(): Unit = runTest {
        stubUser()
        vm = buildVm()

        vm.onConfirmLogout()

        verify(signOutUseCase, never()).invoke()
        assertFalse(vm.sessionClosed.value)
    }

    @Test
    fun `onDismissDialog closes the logout dialog`(): Unit = runTest {
        stubUser()
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onLogoutClicked()
            runCurrent()
            vm.onDismissDialog()
            runCurrent()

            assertEquals(SettingsDialogState.Hidden, expectMostRecentItem().dialog())
        }
    }

    @Test
    fun `delete account is not submitted with an empty password`(): Unit = runTest {
        stubUser()
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onDeleteAccountClicked()
            vm.onConfirmDeleteAccount()
            runCurrent()

            assertEquals(SettingsDialogState.DeletingAccount(), expectMostRecentItem().dialog())
            verify(deleteAccountUseCase, never()).invoke(any())
        }
    }

    @Test
    fun `delete account success closes the session`(): Unit = runTest {
        stubUser()
        whenever(deleteAccountUseCase.invoke(any())).doReturn(ResultManage.Success(Unit))
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onDeleteAccountClicked()
            vm.onPasswordChange(password)
            vm.onConfirmDeleteAccount()
            runCurrent()

            assertEquals(SettingsDialogState.Hidden, expectMostRecentItem().dialog())
            verify(deleteAccountUseCase).invoke(password)
            assertTrue(vm.sessionClosed.value)
        }
    }

    @Test
    fun `delete account failure keeps the dialog open with the error`(): Unit = runTest {
        stubUser()
        whenever(deleteAccountUseCase.invoke(any()))
            .doReturn(ResultManage.Failure(DomainError.Auth.InvalidCredentials))
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onDeleteAccountClicked()
            vm.onPasswordChange(password)
            vm.onConfirmDeleteAccount()
            runCurrent()

            assertEquals(
                SettingsDialogState.DeletingAccount(
                    password = password,
                    error = DomainError.Auth.InvalidCredentials
                ),
                expectMostRecentItem().dialog()
            )
            assertFalse(vm.sessionClosed.value)
        }
    }

    @Test
    fun `typing the password again clears the error`(): Unit = runTest {
        stubUser()
        whenever(deleteAccountUseCase.invoke(any()))
            .doReturn(ResultManage.Failure(DomainError.Auth.InvalidCredentials))
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onDeleteAccountClicked()
            vm.onPasswordChange(password)
            vm.onConfirmDeleteAccount()
            runCurrent()
            vm.onPasswordChange("otra")
            runCurrent()

            assertEquals(
                SettingsDialogState.DeletingAccount(password = "otra"),
                expectMostRecentItem().dialog()
            )
        }
    }

    @Test
    fun `onDismissDialog is ignored while deleting the account`(): Unit = runTest {
        stubUser()
        whenever(deleteAccountUseCase.invoke(any())).doSuspendableAnswer { awaitCancellation() }
        vm = buildVm()

        vm.state.test {
            skipItems(2)
            vm.onDeleteAccountClicked()
            vm.onPasswordChange(password)
            vm.onConfirmDeleteAccount()
            runCurrent()
            vm.onDismissDialog()
            runCurrent()

            assertEquals(
                SettingsDialogState.DeletingAccount(password = password, isDeleting = true),
                expectMostRecentItem().dialog()
            )
        }
    }
}
