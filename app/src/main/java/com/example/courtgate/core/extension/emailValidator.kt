package com.example.courtgate.core.extension

import android.util.Patterns

fun String.emailValidator(): String? {
    if (this.isEmpty()) return "Escribe el email"
    return if (!Patterns.EMAIL_ADDRESS.matcher(this).matches()) {
        "El email no es válido"
    } else null
}

/*
class EmailMatcherImpl : EmailMatcher {
    override fun isValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}*/
