package com.example.courtgate.ui.presentation.booking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.courtgate.ResultCourt
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.FreeHoursOfCourt
import com.example.courtgate.ui.presentation.core.FlowSheetState
import com.example.courtgate.ui.presentation.core.canDismiss

// Sheet de nueva reserva: actúa sobre la hora elegida
typealias NewBookingFlowState = FlowSheetState<String>

class BookingStateScreen(
    private val state: ResultCourt<BookingState>
) {
    private val booking: BookingState? = (state as? ResultCourt.Success)?.data

    val sheet: SheetUiState? = booking
        ?.takeIf { it.newBookingFlowState !is FlowSheetState.Hidden }
        ?.let { b ->
            SheetUiState(
                state = b.newBookingFlowState,
                court = b.requestedCourt,
                isSelectedHourStillFree = b.isSelectedHourStillFree,
                canDismiss = b.newBookingFlowState.canDismiss
            )
        }

    val isSheetSucceeded: Boolean = sheet?.state is FlowSheetState.Succeeded

}

@Composable
fun rememberBookingStateScreen(state: ResultCourt<BookingState>): BookingStateScreen =
    remember(state) { BookingStateScreen(state) }


data class BookingState(
    val freeHoursOfCourt: List<FreeHoursOfCourt> = emptyList(),
    val requestedCourt: Court?,
    val newBookingFlowState: NewBookingFlowState = FlowSheetState.Hidden,
    val selectedHourToBook: String? = null,
    val isSelectedHourStillFree: Boolean = true
)

data class SheetUiState(
    val state: NewBookingFlowState,
    val court: Court?,
    val isSelectedHourStillFree: Boolean,
    val canDismiss: Boolean
)
