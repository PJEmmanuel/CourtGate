package com.example.courtgate.ui.presentation.login

import com.example.courtgate.domain.models.DomainError

data class LoginState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val authError: DomainError? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isSubmitEnabled: Boolean = false,
    val resetPassword: ResetPasswordState? = null // null = diálogo cerrado
) {
    val isFormValid: Boolean
        get() = email.isNotEmpty() && password.isNotEmpty() && emailError == null && passwordError == null
}

data class ResetPasswordState(
    val email: String = "",
    val emailError: String? = null,
    val error: DomainError? = null,
    val isLoading: Boolean = false,
    val isSent: Boolean = false
) {
    val isSubmitEnabled: Boolean
        get() = email.isNotEmpty() && emailError == null && !isLoading
}
