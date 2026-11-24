package com.example.courtgate.ui.presentation.myBookings

import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.ui.presentation.core.FlowSheetState

// Sheet de cancelar reserva: actúa sobre la reserva elegida
typealias MyBookingFlowState = FlowSheetState<CourtBooking>

data class MyBookingsState(
    val myBookingList: List<CourtBooking> = emptyList(),
    val myBookingFlowState: MyBookingFlowState = FlowSheetState.Hidden
)
