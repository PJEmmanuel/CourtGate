package com.example.courtgate.core.extension

private val EMAIL_REGEX = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")

fun String.emailValidator(): FieldError? {
    if (this.isEmpty()) return FieldError.EmptyEmail
    return if (!EMAIL_REGEX.matches(this)) {
        FieldError.InvalidEmail
    } else null
}
