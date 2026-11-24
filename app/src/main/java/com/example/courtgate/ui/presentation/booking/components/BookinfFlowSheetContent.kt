package com.example.courtgate.ui.presentation.booking.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.courtgate.R
import com.example.courtgate.domain.models.Court
import com.example.courtgate.ui.presentation.booking.NewBookingFlowState
import com.example.courtgate.ui.presentation.core.FlowSheetContent
import com.example.courtgate.ui.presentation.core.FlowSheetTexts
import com.example.courtgate.ui.presentation.core.targetOrNull

private fun bookingTexts(isSelectedHourStillFree: Boolean) = FlowSheetTexts(
    confirmTitle = if (isSelectedHourStillFree) R.string.booking_confirm_title
    else R.string.booking_hour_unavailable,
    confirmAction = R.string.booking_action_confirm,
    confirmDismissAction = R.string.booking_action_cancel,
    submitting = R.string.booking_submitting,
    failedTitle = R.string.booking_failed_title,
    succeededTitle = R.string.booking_succeeded_title,
)

@Composable
fun BookingFlowSheetContent(
    state: NewBookingFlowState,
    isSelectedHourStillFree: Boolean,
    court: Court?,
    onConfirm: () -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
) {
    FlowSheetContent(
        state = state,
        texts = bookingTexts(isSelectedHourStillFree),
        onConfirm = onConfirm,
        onRetry = onRetry,
        onDismiss = onDismiss,
        isActionEnabled = isSelectedHourStillFree,
    ) {
        Text(stringResource(R.string.booking_court_label, court?.name ?: "-"))
        Text(stringResource(R.string.booking_hour_label, state.targetOrNull ?: "-"))
        Text(stringResource(R.string.booking_price_label, court?.price?.toString() ?: "-"))
    }
}
