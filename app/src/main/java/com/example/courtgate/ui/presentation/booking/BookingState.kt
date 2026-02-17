package com.example.courtgate.ui.presentation.booking

import com.example.courtgate.home.domain.models.CourtList
import com.example.courtgate.home.domain.models.FreeHoursOfCourt

sealed class BookingUiState<out T> {
    object Idle : BookingUiState<Nothing>()
    object Loading : BookingUiState<Nothing>()
    data class Success<T>(val data: T) : BookingUiState<T>()
    data class Error(val message: String) : BookingUiState<Nothing>()
}

data class BookingState(
    val code: String = "",
    val freeHoursOfCourt: List<FreeHoursOfCourt> = emptyList(),
    val requestedCourt: CourtList?,
    val timeOffer: List<String> = listOf("08:00","09:30","11:00","12:30","16:00","17:30"),
    val selectedHourToBook: String = "",
    val showConfirmationDialog: Boolean = false,
    )
