package com.example.courtgate.ui.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.core.extension.emailValidator
import com.example.courtgate.core.extension.passValidator
import com.example.courtgate.usecases.authentication.SignUpWhitEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpWhitEmailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SignUpState>(SignUpState())
    val state: StateFlow<SignUpState> = _state.asStateFlow()

    fun signUp() {
        viewModelScope.launch {
            signUpUseCase.invoke(
                email = _state.value.email, password = _state.value.password
            ).onSuccess {
                _state.update { it.copy(isSignedUpIn = true) }
            }.onFailure { throwable ->
                _state.update { it.copy(emailError = throwable.message) }
            }
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


/*var state by mutableStateOf(SignUpState())
    private set

fun onEvent(event: SignUpEvent) {
    when (event) {
        is SignUpEvent.EmailChange -> state = state.copy(email = event.email)
        is SignUpEvent.PasswordChange -> state = state.copy(password = event.password)
        SignUpEvent.LogIn -> state = state.copy(LogIn = true)
        SignUpEvent.SignUp -> signUp()
    }
}

private fun signUp() {
    state = state.copy(
        emailError = null,
        passwordError = null
    )
    if (!signUpUseCases.validateEmailUseCase(state.email)) {
        state = state.copy(
            emailError = "El email no es valido"
        )
    }
    val passwordResult = signUpUseCases.validatePasswordUseCase(state.password)
    state = state.copy(
        passwordError = PasswordErrorParser.parserError(passwordResult)
    )

    if (state.emailError == null && state.passwordError == null) {
        state = state.copy(
            isLoading = true
        )
        viewModelScope.launch {
            signUpUseCases.signUpWhitEmailUseCase(state.email, state.password).onSuccess {
                state = state.copy(
                    isSignedUpIn = true
                )
            }.onFailure {
                state = state.copy(
                    emailError = it.message
                )
            }
            state = state.copy(
                isLoading = false
            )
        }
    }
}*/

