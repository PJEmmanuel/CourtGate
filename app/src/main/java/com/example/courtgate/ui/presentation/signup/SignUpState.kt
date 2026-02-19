package com.example.courtgate.ui.presentation.signup

data class SignUpState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false,// Sin uso
    val isSignedUpIn: Boolean = false,//De momento es un trigger
    val logIn: Boolean = false // Sin uso
)
