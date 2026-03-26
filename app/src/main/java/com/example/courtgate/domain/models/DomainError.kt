package com.example.courtgate.domain.models

sealed interface DomainError {

    // Errores de Autenticación (Firebase Auth)
    sealed interface Auth : DomainError {
        data object InvalidCredentials : Auth        // Email o password incorrectos
        data object UserCollision : Auth             // El usuario ya existe
        data object WeakPassword : Auth              // Contraseña no segura
        data object UserNotFound : Auth              // El usuario no existe
        data object NetworkError : Auth              // Sin internet durante login/signup
        data object UnknownAuthError : Auth          // Error genérico
    }

    // Errores de Base de Datos Remota (Firestore)
    sealed interface Remote : DomainError {
        data object AccessDenied : Remote            // Errores de reglas de seguridad (403)
        data object NotFound : Remote                // Documento no encontrado
        data object ServerError : Remote             // Fallo en los servidores de Google
        data object UnknownRemoteError : Remote
    }

    // Errores de Base de Datos Local (Room)
    sealed interface Local : DomainError {
        data object DiskFull : Local                 // No hay espacio en el móvil
        data object ConstraintViolation : Local      // Error de IDs duplicados o llaves foráneas
        data object DatabaseCorrupted : Local        // La DB se ha roto
        data object UnknownLocalError : Local
    }

    // Errores de dominio de Pistas (Court SSOT)
    sealed interface Court : DomainError {
        data object NoCourtsAvailable : Court
    }
}

// Dentro de un Flow no se puede emitir un tipo de error tipado directamente,
class DomainException(val error: DomainError) : Exception()

// TODO: usar @StringRes
fun DomainError.toUserMessage(): String = when (this) {
    DomainError.Remote.AccessDenied       -> "No tienes permisos para acceder"
    DomainError.Remote.ServerError        -> "Error del servidor. Inténtalo más tarde"
    DomainError.Remote.NotFound           -> "Datos no encontrados"
    DomainError.Remote.UnknownRemoteError -> "Error de conexión"
    DomainError.Local.DiskFull            -> "Sin espacio en el dispositivo"
    DomainError.Local.DatabaseCorrupted   -> "Error interno. Reinstala la app"
    DomainError.Local.ConstraintViolation -> "Error interno de datos"
    DomainError.Local.UnknownLocalError   -> "Error local inesperado"
    DomainError.Court.NoCourtsAvailable   -> "No hay pistas disponibles"
    // Auth errors - genérico aquí, cada pantalla de auth puede tener su propio mapper
    DomainError.Auth.InvalidCredentials   -> "Credenciales incorrectas"
    DomainError.Auth.UserCollision        -> "El usuario ya existe"
    DomainError.Auth.WeakPassword         -> "La contraseña no es segura"
    DomainError.Auth.UserNotFound         -> "Usuario no encontrado"
    DomainError.Auth.NetworkError         -> "Sin conexión a internet"
    DomainError.Auth.UnknownAuthError     -> "Error de autenticación"
}