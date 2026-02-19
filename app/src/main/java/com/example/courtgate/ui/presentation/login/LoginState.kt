package com.example.courtgate.ui.presentation.login

data class LoginState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val signUp : Boolean = false, //No se usa
    val isLoggedIn: Boolean = false, // Para pasar a pantalla home
    val isLoading: Boolean = false //No se usa
)
