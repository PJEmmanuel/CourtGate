package com.example.courtgate

sealed interface ResultManage<out T, out E> {
    data class Success<T>(val data: T) : ResultManage<T, Nothing>
    data class Failure<E>(val error: E) : ResultManage<Nothing, E>
}