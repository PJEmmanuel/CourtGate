package com.example.courtgate.ui.presentation.core

import androidx.annotation.StringRes
import com.example.courtgate.R
import com.example.courtgate.core.extension.FieldError
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException

// Único punto de la UI que convierte un Throwable en DomainError.
// `unknownError` es el genérico de cada pantalla: remoto, local o de autenticación.
fun Throwable.toDomainError(unknownError: DomainError): DomainError = when (this) {
    is DomainException -> error
    else -> unknownError
}

@StringRes
fun DomainError.asStringRes(): Int = when (this) {
    DomainError.Remote.AccessDenied -> R.string.error_access_denied
    DomainError.Remote.ServerError -> R.string.error_server
    DomainError.Remote.NotFound -> R.string.error_not_found
    DomainError.Remote.UnknownRemoteError -> R.string.error_unknown_remote
    DomainError.Local.DiskFull -> R.string.error_disk_full
    DomainError.Local.DatabaseCorrupted -> R.string.error_db_corrupted
    DomainError.Local.ConstraintViolation -> R.string.error_db_constraint
    DomainError.Local.UnknownLocalError -> R.string.error_db_unknown
    DomainError.Court.NoCourtsAvailable -> R.string.error_no_courts
    DomainError.Auth.InvalidCredentials -> R.string.error_invalid_credentials
    DomainError.Auth.UserCollision -> R.string.error_user_collision
    DomainError.Auth.WeakPassword -> R.string.error_weak_password
    DomainError.Auth.UserNotFound -> R.string.error_user_not_found
    DomainError.Auth.NetworkError -> R.string.error_network
    DomainError.Auth.TooManyRequests -> R.string.error_too_many_requests
    DomainError.Auth.SessionExpired -> R.string.error_session_expired
    DomainError.Auth.RecentLoginRequired -> R.string.error_recent_login_required
    DomainError.Auth.UnknownAuthError -> R.string.error_auth_unknown
    DomainError.Booking.SlotAlreadyTaken -> R.string.error_booking_slot_taken
    DomainError.Booking.SlotInPast -> R.string.slot_in_past
}

@StringRes
fun FieldError.asStringRes(): Int = when (this) {
    FieldError.EmptyEmail -> R.string.field_error_empty_email
    FieldError.InvalidEmail -> R.string.field_error_invalid_email
    FieldError.EmptyPassword -> R.string.field_error_empty_password
    FieldError.PasswordTooShort -> R.string.field_error_password_too_short
    FieldError.PasswordNoLowercase -> R.string.field_error_password_no_lowercase
    FieldError.PasswordNoUppercase -> R.string.field_error_password_no_uppercase
    FieldError.PasswordNoDigit -> R.string.field_error_password_no_digit
}
