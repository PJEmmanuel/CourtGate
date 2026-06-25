package com.example.courtgate.ui.presentation.myBookings

import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.domain.models.DomainError

data class MyBookingsState(
    val myBookingList: List<CourtBooking> = emptyList(),
    val myBookingFlowState: MyBookingFlowState = MyBookingFlowState.Hidden
)

sealed interface MyBookingFlowState {
    data object Hidden : MyBookingFlowState
    data class Confirming(val booking: CourtBooking) : MyBookingFlowState
    data class Submitting(val booking: CourtBooking) : MyBookingFlowState
    data class Failed(val booking: CourtBooking, val error: DomainError) : MyBookingFlowState
    data class Succeeded(val booking: CourtBooking) : MyBookingFlowState
}
