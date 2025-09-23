package com.example.courtgate.home.presentation.find

import com.example.courtgate.home.domain.models.CourtList
import com.example.courtgate.home.domain.models.FilterOption
import java.time.ZonedDateTime

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    data class Loading<T>(val data: T) : UiState<T>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

data class FindState(
    val filterList: List<FilterOption> = emptyList(),
    val mainCourtList: List<CourtList> = emptyList(),
    val filteredCourtList: List<CourtList> = emptyList(),
    val selectedCourt: CourtList? = null,
    val selectedLocate: String? = "",
    val selectedHour: String = "",
    val onSelectedLocated: Boolean = false,
    val selectedDate: ZonedDateTime = ZonedDateTime.now(),
    val mainDate: ZonedDateTime = ZonedDateTime.now(),
)
