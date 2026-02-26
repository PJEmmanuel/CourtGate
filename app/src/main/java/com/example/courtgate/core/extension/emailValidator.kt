package com.example.courtgate.core.extension

private val EMAIL_REGEX = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")

fun String.emailValidator(): String? {
    if (this.isEmpty()) return "Escribe el email"
    return if (!EMAIL_REGEX.matches(this)) {
        "El email no es válido"
    } else null
}
