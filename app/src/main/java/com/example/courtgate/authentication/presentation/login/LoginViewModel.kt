package com.example.courtgate.authentication.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.authentication.domain.usecase.LoginUseCases
import com.example.courtgate.authentication.presentation.utils.PasswordErrorParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCases: LoginUseCases
) : ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChange -> state = state.copy(email = event.email)
            LoginEvent.Login -> login()
            is LoginEvent.PasswordChange -> state = state.copy(password = event.password)
            LoginEvent.SignUp -> state = state.copy(signUp = true)
        }
    }

    private fun login() {
        state = state.copy(
            emailError = null,
            passwordError = null
        )
        if (!loginUseCases.validateEmailUseCase(state.email)) {
            state = state.copy(emailError = "El email no es válido")
        }

        val passwordResult = loginUseCases.validatePasswordUseCase(state.password)
        state = state.copy(passwordError = PasswordErrorParser.parserError(passwordResult))

        if (state.emailError == null && state.passwordError == null) {

            state = state.copy(isLoading = true)

            viewModelScope.launch {
                loginUseCases.loginWithEmailUseCase(state.email, state.password).onSuccess {
                    state = state.copy(isLoggedIn = true)
                }.onFailure {
                    state =
                        state.copy(emailError = it.message) //TODO puede ponerse un mensaje personalizado...
                }
            }
            state = state.copy(isLoading = false)
        }
    }
}