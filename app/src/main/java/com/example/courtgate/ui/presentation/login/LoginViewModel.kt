package com.example.courtgate.ui.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.core.extension.emailValidator
import com.example.courtgate.core.extension.passValidator
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.usecases.authentication.LoginWithEmailUseCase
import com.example.courtgate.usecases.authentication.SendPasswordResetEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isSubmitEnabled = false, authError = null) }
            loginWithEmailUseCase.invoke(
                email = _state.value.email, password = _state.value.password
            ).onSuccess {
                _state.update { it.copy(isLoggedIn = true) }
            }.onFailure { throwable ->
                _state.update { it.copy(authError = mapError(throwable)) }
            }
            _state.update { it.copy(isLoading = false, isSubmitEnabled = it.isFormValid) }
        }
    }

    fun onEmailChange(email: String) {
        _state.update {
            val updated = it.copy(email = email, emailError = email.emailValidator(), authError = null)
            updated.copy(isSubmitEnabled = updated.isFormValid)
        }
    }

    fun onPasswordChange(password: String) {
        _state.update {
            val updated = it.copy(password = password, passwordError = password.passValidator(), authError = null)
            updated.copy(isSubmitEnabled = updated.isFormValid)
        }
    }

    // Forgot password: abre el diálogo con el email que ya haya escrito el usuario
    fun onForgotPassword() {
        val email = _state.value.email
        _state.update {
            it.copy(
                resetPassword = ResetPasswordState(
                    email = email,
                    emailError = if (email.isEmpty()) null else email.emailValidator()
                )
            )
        }
    }

    fun onResetEmailChange(email: String) {
        _state.update {
            it.copy(
                resetPassword = it.resetPassword?.copy(
                    email = email,
                    emailError = email.emailValidator(),
                    error = null
                )
            )
        }
    }

    fun sendPasswordReset() {
        val reset = _state.value.resetPassword ?: return
        if (!reset.isSubmitEnabled) return
        viewModelScope.launch {
            _state.update { it.copy(resetPassword = it.resetPassword?.copy(isLoading = true, error = null)) }
            sendPasswordResetEmailUseCase.invoke(reset.email)
                .onSuccess {
                    _state.update { it.copy(resetPassword = it.resetPassword?.copy(isLoading = false, isSent = true)) }
                }.onFailure { throwable ->
                    val error = mapError(throwable)
                    // No revelamos si el email está registrado: se muestra el mismo mensaje que al enviarlo
                    val isSent = error == DomainError.Auth.UserNotFound
                    _state.update {
                        it.copy(
                            resetPassword = it.resetPassword?.copy(
                                isLoading = false,
                                isSent = isSent,
                                error = if (isSent) null else error
                            )
                        )
                    }
                }
        }
    }

    fun onDismissResetPassword() {
        _state.update { it.copy(resetPassword = null) }
    }

    private fun mapError(e: Throwable): DomainError = when (e) {
        is DomainException -> e.error
        else -> DomainError.Auth.UnknownAuthError
    }
}
