package com.example.courtgate.authentication.presentation.signup

sealed interface SignUpEvent {
    data class EmailChange(val email: String) : SignUpEvent
    data class PasswordChange(val password: String) : SignUpEvent
    object LogIn : SignUpEvent //TODO: Revisar uso
    object SignUp : SignUpEvent //TODO: Revisar uso
}