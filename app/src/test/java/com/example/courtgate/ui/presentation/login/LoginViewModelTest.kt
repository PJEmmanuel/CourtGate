package com.example.courtgate.ui.presentation.login

import com.example.courtgate.core.CoroutinesTestRule
import com.example.courtgate.domain.models.User
import com.example.courtgate.ui.presentation.signup.SignUpViewModel
import com.example.courtgate.usecases.authentication.LoginWithEmailUseCase
import com.example.courtgate.usecases.authentication.SignUpWhitEmailUseCase
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
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var loginWithEmailUseCase: LoginWithEmailUseCase

    private lateinit var vm: LoginViewModel

    private val wrongEmail = "asd.com"
    private val correctEmail = "asd@ASD.com"
    private val correctPass = "asdASD123"
    private val wrongPass = "asdA"
    private val uid = "asd123"

    @Before
    fun setUp() {
        vm = LoginViewModel(loginWithEmailUseCase)
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
        vm.onEmailChange(correctPass)

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