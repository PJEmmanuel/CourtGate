package com.example.courtgate.ui.presentation.core

import com.example.courtgate.ResultCourt
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/*
 * Carga que se relanza cada vez que llega un valor nuevo del flow de origen
 * (parámetros de la pantalla + contador de reintentos).
 *
 * - Un error termina la carga actual, pero no el flow resultante: el siguiente valor
 *   del origen (p. ej. pulsar "Reintentar") vuelve a lanzar `load`.
 * - Cuenta los fallos seguidos para que la UI cambie el mensaje si no deja de fallar.
 * - Tras un error emite Loading al relanzar, para que se vea que se está reintentando.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <P, T> Flow<P>.loadLatest(
    unknownError: DomainError,
    load: (P) -> Flow<T>
): Flow<ResultCourt<T>> {
    var failedAttempts = 0
    return flatMapLatest { params ->
        load(params)
            .map<T, ResultCourt<T>> { data ->
                failedAttempts = 0
                ResultCourt.Success(data)
            }
            .onStart { if (failedAttempts > 0) emit(ResultCourt.Loading) }
            .catch { e ->
                failedAttempts++
                emit(ResultCourt.Error(e.toDomainError(unknownError), failedAttempts))
            }
    }
}

fun Throwable.toDomainError(unknownError: DomainError): DomainError = when (this) {
    is DomainException -> error
    else -> unknownError
}
