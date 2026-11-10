package com.example.courtgate.ui.presentation.signup

import com.example.courtgate.domain.models.DomainError

data class SignUpState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val authError: DomainError? = null,
    val isLoading: Boolean = false,
    val isSignedUpIn: Boolean = false,
    val isSubmitEnabled: Boolean = false
) {
    val isFormValid: Boolean
        get() = email.isNotEmpty() && password.isNotEmpty() && emailError == null && passwordError == null
}
