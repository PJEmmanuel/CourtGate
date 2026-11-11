package com.example.courtgate.usecases.settings

import com.example.courtgate.ResultManage
import com.example.courtgate.data.AuthenticationRepository
import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.data.MatchRepository
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import javax.inject.Inject

/*
 * Borra la cuenta y los datos del usuario. Orden:
 * 1. Reautenticar: Firebase exige un login reciente para borrar la cuenta.
 * 2. Reservas en Firestore: necesitan la sesión activa para pasar las reglas de seguridad.
 * 3. Resultados locales (Room).
 * 4. La cuenta, la última: si algo falla antes, la cuenta sigue ahí y se puede reintentar entero
 *    (los borrados anteriores son idempotentes).
 */
class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthenticationRepository,
    private val courtRepository: ManageCourtRepository,
    private val matchRepository: MatchRepository,
) {
    suspend operator fun invoke(password: String): ResultManage<Unit, DomainError> {
        val userId = authRepository.getCurrentUserId()
            ?: return ResultManage.Failure(DomainError.Auth.SessionExpired)

        authRepository.reauthenticate(password).onFailure {
            return ResultManage.Failure(it.toAuthError())
        }

        val bookings = courtRepository.deleteAllUserBookings(userId)
        if (bookings is ResultManage.Failure) return bookings

        val results = matchRepository.deleteAllResults()
        if (results is ResultManage.Failure) return results

        authRepository.deleteCurrentUser().onFailure {
            return ResultManage.Failure(it.toAuthError())
        }
        return ResultManage.Success(Unit)
    }

    private fun Throwable.toAuthError(): DomainError =
        (this as? DomainException)?.error ?: DomainError.Auth.UnknownAuthError
}
