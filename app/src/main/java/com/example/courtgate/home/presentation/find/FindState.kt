package com.example.courtgate.home.presentation.find

import com.example.courtgate.home.domain.models.CourtList
import java.time.ZonedDateTime

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

data class FindState(
    val courtList: List<CourtList> = emptyList(),
    val selectedHour: String = "",
    val selectedType: String = "",
    val onHourSelected: String = "",
    val mainDate: ZonedDateTime = ZonedDateTime.now()
)
