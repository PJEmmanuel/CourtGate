package com.example.courtgate.ui.presentation.signup

data class SignUpState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isSignedUpIn: Boolean = false,
    val isSubmitEnabled: Boolean = false
) {
    val isFormValid: Boolean
        get() = email.isNotEmpty() && password.isNotEmpty() && emailError == null && passwordError == null
}
