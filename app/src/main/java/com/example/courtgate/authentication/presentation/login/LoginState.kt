package com.example.courtgate.authentication.presentation.login

data class LoginState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val signUp : Boolean = false,
    val isLoggedIn: Boolean = false, // Para la pantalla de carga
    val isLoading: Boolean = false
)
