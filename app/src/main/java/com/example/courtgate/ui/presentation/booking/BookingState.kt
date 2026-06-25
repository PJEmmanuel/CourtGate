package com.example.courtgate.ui.presentation.booking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.courtgate.ResultCourt
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.FreeHoursOfCourt

class BookingStateScreen(
    private val state: ResultCourt<BookingState>
) {
    private val booking: BookingState? = (state as? ResultCourt.Success)?.data

    val sheet: SheetUiState? = booking
        ?.takeIf { it.newBookingFlowState !is NewBookingFlowState.Hidden }
        ?.let { b ->
            SheetUiState(
                state = b.newBookingFlowState,
                court = b.requestedCourt,
                selectedHour = b.selectedHourToBook,
                isSelectedHourStillFree = b.isSelectedHourStillFree,
                canDismiss = b.newBookingFlowState !is NewBookingFlowState.Submitting &&
                        b.newBookingFlowState !is NewBookingFlowState.Succeeded
            )
        }

    val isSheetSucceeded: Boolean = sheet?.state is NewBookingFlowState.Succeeded

}

@Composable
fun rememberBookingStateScreen(state: ResultCourt<BookingState>): BookingStateScreen =
    remember(state) { BookingStateScreen(state) }


data class BookingState(
    val freeHoursOfCourt: List<FreeHoursOfCourt> = emptyList(),
    val requestedCourt: Court?,
    val newBookingFlowState: NewBookingFlowState = NewBookingFlowState.Hidden,
    val selectedHourToBook: String? = null,
    val isSelectedHourStillFree: Boolean = true
)

data class SheetUiState(
    val state: NewBookingFlowState,
    val court: Court?,
    val selectedHour: String?,
    val isSelectedHourStillFree: Boolean,
    val canDismiss: Boolean
)

sealed interface NewBookingFlowState {
    data object Hidden : NewBookingFlowState
    data object Confirming : NewBookingFlowState
    data object Submitting : NewBookingFlowState
    data class Failed(val error: DomainError) : NewBookingFlowState
    data object Succeeded : NewBookingFlowState
}
