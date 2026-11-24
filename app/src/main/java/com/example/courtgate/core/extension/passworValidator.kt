package com.example.courtgate.core.extension

fun String.passValidator(): FieldError? {
    if (this.isEmpty()) return FieldError.EmptyPassword
    if (this.length < 8) {
        return FieldError.PasswordTooShort
    }

    if (!this.any { it.isLowerCase() }) {
        return FieldError.PasswordNoLowercase
    }

    if (!this.any { it.isUpperCase() }) {
        return FieldError.PasswordNoUppercase
    }

    if (!this.any { it.isDigit() }) {
        return FieldError.PasswordNoDigit
    }
    return null
}
