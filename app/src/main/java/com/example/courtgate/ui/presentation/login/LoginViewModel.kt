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
            _state.update { it.copy(isLoading = true, isSubmitEnabled = false) }
            loginWithEmailUseCase.invoke(
                email = _state.value.email, password = _state.value.password
            ).onSuccess {
                _state.update { it.copy(isLoggedIn = true) }
            }.onFailure { throwable ->
                _state.update { it.copy(emailError = throwable.message) }
            }
            _state.update { it.copy(isLoading = false, isSubmitEnabled = it.isFormValid) }
        }
    }

    fun onEmailChange(email: String) {
        _state.update {
            val updated = it.copy(email = email, emailError = email.emailValidator())
            updated.copy(isSubmitEnabled = updated.isFormValid)
        }
    }

    fun onPasswordChange(password: String) {
        _state.update {
            val updated = it.copy(password = password, passwordError = password.passValidator())
            updated.copy(isSubmitEnabled = updated.isFormValid)
        }
    }
}
