package com.example.courtgate

import com.example.courtgate.domain.models.DomainError

sealed interface ResultCourt<out T> {
    data class Success<T>(val data: T) : ResultCourt<T>
    // Ahora recibe DomainError tipado en vez de Throwable crudo
    data class Error(val error: DomainError) : ResultCourt<Nothing>
    /* data class Error(val exception: Throwable) : ResultCourt<Nothing> */
    data object Loading : ResultCourt<Nothing>
}

inline fun <T> ResultCourt<T>.ifSuccess(block: (T) -> Unit) {
    if (this is ResultCourt.Success) {
        block(data)
    }
}