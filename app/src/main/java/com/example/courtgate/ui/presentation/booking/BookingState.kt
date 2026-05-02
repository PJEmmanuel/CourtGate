package com.example.courtgate.ui.presentation.booking

import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.FreeHoursOfCourt

data class BookingState(
    val code: String = "",
    val freeHoursOfCourt: List<FreeHoursOfCourt> = emptyList(),
    val requestedCourt: Court?,
    val newBookingFlowState: NewBookingFlowState = NewBookingFlowState.Hidden
    //val selectedHourToBook: String = "",
    //val showConfirmationDialog: Boolean = false,
)

sealed interface NewBookingFlowState {
    data object Hidden : NewBookingFlowState
    data object Confirming : NewBookingFlowState
    data object Submitting : NewBookingFlowState
    data class Failed(val error: DomainError) : NewBookingFlowState
    data object Succeeded : NewBookingFlowState
}
