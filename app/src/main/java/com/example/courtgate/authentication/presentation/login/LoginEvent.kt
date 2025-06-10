package com.example.courtgate.authentication.presentation.login

sealed interface LoginEvent {
    data class EmailChange(val email: String) : LoginEvent
    data class PasswordChange(val password: String) : LoginEvent
    object Login : LoginEvent //TODO: Revisar uso
    object SignUp : LoginEvent //TODO: Revisar uso
}