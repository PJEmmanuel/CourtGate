package com.example.courtgate.ui.presentation.myBookings.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.courtgate.R
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.ui.presentation.core.FlowSheetContent
import com.example.courtgate.ui.presentation.core.FlowSheetTexts
import com.example.courtgate.ui.presentation.core.toBookingDateLabel
import com.example.courtgate.ui.presentation.myBookings.MyBookingFlowState

private val cancelBookingTexts = FlowSheetTexts(
    confirmTitle = R.string.my_booking_cancel_title,
    confirmAction = R.string.my_booking_action_cancel_booking,
    confirmDismissAction = R.string.booking_action_back,
    submitting = R.string.my_booking_submitting,
    failedTitle = R.string.my_booking_failed_title,
    succeededTitle = R.string.my_booking_succeeded_title,
)

@Composable
fun MyBookingsFlowSheetContent(
    state: MyBookingFlowState,
    court: CourtBooking,
    onConfirm: () -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
) {
    FlowSheetContent(
        state = state,
        texts = cancelBookingTexts,
        onConfirm = onConfirm,
        onRetry = onRetry,
        onDismiss = onDismiss,
    ) {
        Text(stringResource(R.string.booking_court_label, court.code))
        Text(stringResource(R.string.booking_date_label, court.date.toBookingDateLabel()))
        Text(stringResource(R.string.booking_hour_label, court.hour))
    }
}
