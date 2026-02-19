package com.example.courtgate.ui.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.core.extension.emailValidator
import com.example.courtgate.core.extension.passValidator
import com.example.courtgate.usecases.authentication.LoginWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithEmailUseCase: LoginWithEmailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            loginWithEmailUseCase.invoke(
                email = _state.value.email, password = _state.value.password
            ).onSuccess {
                _state.update { it.copy(isLoggedIn = true) }
            }.onFailure { throwable ->
                _state.update { it.copy(emailError = throwable.message) }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onEmailChange(email: String) {
        _state.update {
            it.copy(email = email, emailError = email.emailValidator())
        }
    }

    fun onPasswordChange(password: String) {
        _state.update {
            it.copy(password = password, passwordError = password.passValidator())
        }
    }
}

    /*var state by mutableStateOf(LoginState())
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
                        state.copy(emailError = it.message)
                }
            }
            state =
                state.copy(isLoading = false)//TODO esto no va aquí? runs right after launch {}, not after the async operation completes (race condition)
        }
    }*/
