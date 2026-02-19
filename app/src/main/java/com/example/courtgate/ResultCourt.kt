package com.example.courtgate

sealed interface ResultCourt<out T> {
    data class Success<T>(val data: T) : ResultCourt<T>
    data class Error(val exception: Throwable) : ResultCourt<Nothing>
    data object Loading : ResultCourt<Nothing>
}

inline fun <T> ResultCourt<T>.ifSuccess(block: (T) -> Unit) {
    if (this is ResultCourt.Success) {
        block(data)
    }
}