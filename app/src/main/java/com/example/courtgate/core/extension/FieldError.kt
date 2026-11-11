package com.example.courtgate.core.extension

// Errores de validación de formularios. El texto lo pone la UI (FieldError.asStringRes()).
enum class FieldError {
    EmptyEmail,
    InvalidEmail,
    EmptyPassword,
    PasswordTooShort,
    PasswordNoLowercase,
    PasswordNoUppercase,
    PasswordNoDigit,
}
