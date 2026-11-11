package com.example.courtgate

import com.example.courtgate.domain.models.DomainError

sealed interface ResultCourt<out T> {
    data class Success<T>(val data: T) : ResultCourt<T>
    // failedAttempts: fallos seguidos desde el último Success (1 = primer fallo)
    data class Error(val error: DomainError, val failedAttempts: Int = 1) : ResultCourt<Nothing>
    data object Loading : ResultCourt<Nothing>
}

inline fun <T> ResultCourt<T>.ifSuccess(block: (T) -> Unit) {
    if (this is ResultCourt.Success) {
        block(data)
    }
}
