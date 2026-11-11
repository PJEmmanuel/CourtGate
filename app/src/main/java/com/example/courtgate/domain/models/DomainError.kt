package com.example.courtgate.domain.models

sealed interface DomainError {

    // Errores de Autenticación (Firebase Auth)
    sealed interface Auth : DomainError {
        data object InvalidCredentials : Auth        // Email o password incorrectos
        data object UserCollision : Auth             // El usuario ya existe
        data object WeakPassword : Auth              // Contraseña no segura
        data object UserNotFound : Auth              // El usuario no existe
        data object NetworkError : Auth              // Sin internet durante login/signup
        data object TooManyRequests : Auth           // Demasiados intentos seguidos
        data object SessionExpired : Auth            // No hay usuario con sesión iniciada
        data object RecentLoginRequired : Auth       // Operación sensible (borrar cuenta) con un login antiguo
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

    // Errires de al reservar (Set booking)
    sealed interface Booking : DomainError {
        data object SlotAlreadyTaken : Booking      // La reserva ya existe(slot ocupado)
        data object SlotInPast       : Booking      // Esa hora ya ha pasado

    }
}

// Dentro de un Flow no se puede emitir un tipo de error tipado directamente,
class DomainException(val error: DomainError) : Exception()

// Errores pasajeros: volver a intentarlo puede funcionar. El resto fallarán igual.
// `when` exhaustivo: un error nuevo obliga a decidir aquí si se puede reintentar.
val DomainError.isRetryable: Boolean
    get() = when (this) {
        DomainError.Auth.NetworkError,
        DomainError.Auth.TooManyRequests,
        DomainError.Auth.UnknownAuthError,
        DomainError.Remote.ServerError,
        DomainError.Remote.UnknownRemoteError,
        DomainError.Local.UnknownLocalError,
        DomainError.Court.NoCourtsAvailable -> true

        DomainError.Auth.InvalidCredentials,
        DomainError.Auth.UserCollision,
        DomainError.Auth.WeakPassword,
        DomainError.Auth.UserNotFound,
        DomainError.Auth.SessionExpired,
        DomainError.Auth.RecentLoginRequired,
        DomainError.Remote.AccessDenied,
        DomainError.Remote.NotFound,
        DomainError.Local.DiskFull,
        DomainError.Local.ConstraintViolation,
        DomainError.Local.DatabaseCorrupted,
        DomainError.Booking.SlotAlreadyTaken,
        DomainError.Booking.SlotInPast -> false
    }