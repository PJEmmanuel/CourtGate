package com.example.courtgate.home.presentation.find

import com.example.courtgate.home.domain.models.CourtList
import java.time.ZonedDateTime

sealed class LoadingState<out T> {
    object Idle : LoadingState<Nothing>()
    object Loading : LoadingState<Nothing>()
    data class Success<T>(val data: T) : LoadingState<T>()
    data class Error(val message: String) : LoadingState<Nothing>()
}

data class FindState(
    val courtList: LoadingState<List<CourtList>> = LoadingState.Idle,
    val selectedHour: String = "",
    val selectedType: String = "",
    val onHourSelected: String = "",
    val mainDate: ZonedDateTime = ZonedDateTime.now()
)
