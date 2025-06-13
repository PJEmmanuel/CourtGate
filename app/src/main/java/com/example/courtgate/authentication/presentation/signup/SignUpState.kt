package com.example.courtgate.authentication.presentation.signup

data class SignUpState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false,// Para la pantalla de carga
    val isSignedUpIn: Boolean = false,
    val LogIn: Boolean = false
)
