package com.example.courtgate.core.extension

fun String.passValidator(): String? {
    if (this.isEmpty()) return null
    if (this.length < 8) {
        return "La contraseña tiene que tener al menos 8 caracteres"
    }

    if (!this.any { it.isLowerCase() }) {
        return "La contraseña tiene que tener al menos 1 caracter en minuscula"
    }

    if (!this.any { it.isUpperCase() }) {
        return "La contraseña tiene que tener al menos 1 caracter en mayuscula"
    }

    if (!this.any { it.isDigit() }) {
        return "La contraseña tiene que tener al menos 1 numero"
    }
    return null
}

/*class ValidatePasswordUseCase {
    operator fun invoke(password: String): PasswordResult {
        if (password.length < 8) {
            return PasswordResult.INVALID_LENGTH
        }

        if (!password.any { it.isLowerCase() }) {
            return PasswordResult.INVALID_LOWERCASE
        }

        if (!password.any { it.isUpperCase() }) {
            return PasswordResult.INVALID_UPPERCASE
        }

        if (!password.any { it.isDigit() }) {
            return PasswordResult.INVALID_DIGITS
        }
        return PasswordResult.VALID
    }
}*/

/*enum class PasswordResult {
    VALID,
    INVALID_LOWERCASE,
    INVALID_UPPERCASE,
    INVALID_DIGITS,
    INVALID_LENGTH
}

fun parserError(error: PasswordResult): String? {
    return when (error) {
        PasswordResult.VALID -> null
        PasswordResult.INVALID_LOWERCASE -> "La contraseña tiene que tener al menos 1 caracter en minuscula"
        PasswordResult.INVALID_UPPERCASE -> "La contraseña tiene que tener al menos 1 caracter en mayuscula"
        PasswordResult.INVALID_DIGITS -> "La contraseña tiene que tener al menos 1 numero"
        PasswordResult.INVALID_LENGTH -> "La contraseña tiene que tener al menos 8 caracteres"
    }
}*/
