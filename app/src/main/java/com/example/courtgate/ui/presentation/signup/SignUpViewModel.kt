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
            _state.update { it.copy(isLoading = true, isSubmitEnabled = false) }
            signUpUseCase.invoke(
                email = _state.value.email, password = _state.value.password
            ).onSuccess {
                _state.update { it.copy(isSignedUpIn = true) }
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
