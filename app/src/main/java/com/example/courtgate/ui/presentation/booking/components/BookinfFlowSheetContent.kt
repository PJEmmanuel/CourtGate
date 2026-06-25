package com.example.courtgate.ui.presentation.booking.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.courtgate.R
import com.example.courtgate.domain.models.Court
import com.example.courtgate.ui.presentation.booking.NewBookingFlowState
import com.example.courtgate.ui.presentation.core.asStringRes

@Composable
fun BookingFlowSheetContent(
    state: NewBookingFlowState,
    isSelectedHourStillFree: Boolean,
    court: Court?,
    selectedHour: String?,
    onConfirm: () -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (state) {
            NewBookingFlowState.Hidden -> Unit

            NewBookingFlowState.Confirming -> {
                Text(
                    stringResource(
                        if (isSelectedHourStillFree) R.string.booking_confirm_title
                        else R.string.booking_hour_unavailable
                    ),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.booking_court_label, court?.name ?: "-"))
                Text(stringResource(R.string.booking_hour_label, selectedHour ?: "-"))
                Text(stringResource(R.string.booking_price_label, court?.price?.toString() ?: "-"))
                Spacer(Modifier.height(16.dp))
                Row {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.booking_action_cancel))
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onConfirm,
                        enabled = isSelectedHourStillFree
                    ) { Text(stringResource(R.string.booking_action_confirm)) }
                }
            }

            NewBookingFlowState.Submitting -> {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.booking_submitting))
            }

            is NewBookingFlowState.Failed -> {
                Text(
                    stringResource(R.string.booking_failed_title),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
                Text(stringResource(state.error.asStringRes()))
                Spacer(Modifier.height(16.dp))
                Row {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.booking_action_back))
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = onRetry, enabled = isSelectedHourStillFree) {
                        Text(stringResource(R.string.booking_action_retry))
                    }
                }
            }

            NewBookingFlowState.Succeeded -> {
                Text(
                    stringResource(R.string.booking_succeeded_title),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}