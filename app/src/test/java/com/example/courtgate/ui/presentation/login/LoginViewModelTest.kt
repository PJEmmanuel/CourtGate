package com.example.courtgate.ui.presentation.login

import com.example.courtgate.core.CoroutinesTestRule
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.domain.models.User
import com.example.courtgate.usecases.authentication.LoginWithEmailUseCase
import com.example.courtgate.usecases.authentication.SendPasswordResetEmailUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var loginWithEmailUseCase: LoginWithEmailUseCase

    @Mock
    lateinit var sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase

    private lateinit var vm: LoginViewModel

    private val wrongEmail = "asd.com"
    private val correctEmail = "asd@ASD.com"
    private val correctPass = "asdASD123"
    private val wrongPass = "asdA"
    private val uid = "asd123"

    @Before
    fun setUp() {
        vm = LoginViewModel(loginWithEmailUseCase, sendPasswordResetEmailUseCase)
    }

    @Test
    fun `InitialState with default values`() {
        val result = vm.state.value

        assertEquals("", result.email)
        assertEquals("", result.password)
        assertEquals(false, result.isLoading)
        assertEquals(false, result.isLoggedIn)
        assertEquals(false, result.isSubmitEnabled)
    }

    @Test
    fun `check incorrect email format when email is empty`() {
        vm.onEmailChange("")

        assertEquals("Escribe el email", vm.state.value.emailError)
        assertNotNull(vm.state.value.emailError)

    }

    @Test
    fun `check correct email format when email is good`() {
        vm.onEmailChange(correctEmail)

        assertNull(vm.state.value.emailError)
    }

    @Test
    fun `check incorrect email format, then clear error and format email is OK`() {

        vm.onEmailChange(wrongEmail)

        assertEquals("El email no es válido", vm.state.value.emailError)
        assertNotNull(vm.state.value.emailError)

        vm.onEmailChange(correctEmail)

        assertNull(vm.state.value.emailError)

    }

    @Test
    fun `check incorrect password format when password is empty`() {
        vm.onPasswordChange("")

        assertEquals("Escribe la contraseña", vm.state.value.passwordError)
        assertNotNull(vm.state.value.passwordError)

    }

    @Test
    fun `check correct password format when password is good`() {
        vm.onPasswordChange(correctPass)

        assertNull(vm.state.value.passwordError)
    }

    @Test
    fun `check incorrect password format, then clear error and format password is OK`() {

        vm.onPasswordChange(wrongPass)

        assertNotEquals("Escribe la contraseña", vm.state.value.passwordError)
        assertNotNull(vm.state.value.passwordError)

        vm.onPasswordChange(correctPass)

        assertNull(vm.state.value.passwordError)

    }

    @Test
    fun `login success, then isLoggedIn is true`(): Unit = runTest {
        whenever(loginWithEmailUseCase.invoke(correctEmail, correctPass))
            .thenReturn(Result.success(User(uid, correctEmail)))

        vm.onEmailChange(correctEmail)
        vm.onPasswordChange(correctPass)

        vm.login()
        runCurrent()

        assertEquals(true, vm.state.value.isLoggedIn)
        assertEquals(false, vm.state.value.isLoading)
    }

    @Test
    fun `login failure, then authError is set and emailError stays clean`(): Unit = runTest {
        whenever(loginWithEmailUseCase.invoke(correctEmail, correctPass))
            .thenReturn(Result.failure(DomainException(DomainError.Auth.InvalidCredentials)))

        vm.onEmailChange(correctEmail)
        vm.onPasswordChange(correctPass)

        vm.login()
        runCurrent()

        assertEquals(DomainError.Auth.InvalidCredentials, vm.state.value.authError)
        assertNull(vm.state.value.emailError)
        assertEquals(false, vm.state.value.isLoading)
    }

    @Test
    fun `login failure with unexpected exception, then shows generic auth error`(): Unit = runTest {
        whenever(loginWithEmailUseCase.invoke(correctEmail, correctPass))
            .thenReturn(Result.failure(IllegalStateException("internal firebase message")))

        vm.onEmailChange(correctEmail)
        vm.onPasswordChange(correctPass)

        vm.login()
        runCurrent()

        assertEquals(DomainError.Auth.UnknownAuthError, vm.state.value.authError)
    }

    @Test
    fun `editing a field after login failure clears authError`(): Unit = runTest {
        whenever(loginWithEmailUseCase.invoke(correctEmail, correctPass))
            .thenReturn(Result.failure(DomainException(DomainError.Auth.InvalidCredentials)))

        vm.onEmailChange(correctEmail)
        vm.onPasswordChange(correctPass)
        vm.login()
        runCurrent()

        vm.onPasswordChange("${correctPass}4")

        assertNull(vm.state.value.authError)
    }

    @Test
    fun `forgot password opens dialog prefilled with typed email`() {
        vm.onEmailChange(correctEmail)

        vm.onForgotPassword()

        assertEquals(correctEmail, vm.state.value.resetPassword?.email)
        assertNull(vm.state.value.resetPassword?.emailError)
    }

    @Test
    fun `forgot password with empty email opens dialog without error`() {
        vm.onForgotPassword()

        assertEquals("", vm.state.value.resetPassword?.email)
        assertNull(vm.state.value.resetPassword?.emailError)
        assertEquals(false, vm.state.value.resetPassword?.isSubmitEnabled)
    }

    @Test
    fun `send password reset success, then dialog shows sent`(): Unit = runTest {
        whenever(sendPasswordResetEmailUseCase.invoke(correctEmail))
            .thenReturn(Result.success(Unit))

        vm.onForgotPassword()
        vm.onResetEmailChange(correctEmail)
        vm.sendPasswordReset()
        runCurrent()

        verify(sendPasswordResetEmailUseCase).invoke(correctEmail)
        assertEquals(true, vm.state.value.resetPassword?.isSent)
        assertEquals(false, vm.state.value.resetPassword?.isLoading)
    }

    @Test
    fun `send password reset for unknown user, then shows sent without revealing it`(): Unit = runTest {
        whenever(sendPasswordResetEmailUseCase.invoke(correctEmail))
            .thenReturn(Result.failure(DomainException(DomainError.Auth.UserNotFound)))

        vm.onForgotPassword()
        vm.onResetEmailChange(correctEmail)
        vm.sendPasswordReset()
        runCurrent()

        assertEquals(true, vm.state.value.resetPassword?.isSent)
        assertNull(vm.state.value.resetPassword?.error)
    }

    @Test
    fun `send password reset network failure, then error is shown and dialog stays open`(): Unit = runTest {
        whenever(sendPasswordResetEmailUseCase.invoke(correctEmail))
            .thenReturn(Result.failure(DomainException(DomainError.Auth.NetworkError)))

        vm.onForgotPassword()
        vm.onResetEmailChange(correctEmail)
        vm.sendPasswordReset()
        runCurrent()

        assertEquals(DomainError.Auth.NetworkError, vm.state.value.resetPassword?.error)
        assertEquals(false, vm.state.value.resetPassword?.isSent)
    }

    @Test
    fun `send password reset with invalid email does not call use case`(): Unit = runTest {
        vm.onForgotPassword()
        vm.onResetEmailChange(wrongEmail)
        vm.sendPasswordReset()
        runCurrent()

        verifyNoInteractions(sendPasswordResetEmailUseCase)
    }

    @Test
    fun `dismiss password reset closes dialog`() {
        vm.onForgotPassword()

        vm.onDismissResetPassword()

        assertNull(vm.state.value.resetPassword)
    }

    @Test
    fun `isSubmitEnabled is true when email and password are valid`() {
        vm.onEmailChange(correctEmail)
        vm.onPasswordChange(correctPass)

        assertEquals(true, vm.state.value.isSubmitEnabled)
    }

    @Test
    fun `verify that useCase is called with written fields`(): Unit = runTest {

        whenever(loginWithEmailUseCase.invoke(correctEmail, correctPass))
            .thenReturn(Result.success(User(uid, correctEmail)))

        vm.onEmailChange(correctEmail)
        vm.onPasswordChange(correctPass)

        vm.login()
        runCurrent()


        verify(loginWithEmailUseCase).invoke(correctEmail, correctPass)
    }
}