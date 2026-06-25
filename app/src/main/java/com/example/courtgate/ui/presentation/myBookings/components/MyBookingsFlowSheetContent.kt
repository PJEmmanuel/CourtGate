package com.example.courtgate.ui.presentation.myBookings.components

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
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.ui.presentation.core.asStringRes
import com.example.courtgate.ui.presentation.myBookings.MyBookingFlowState

//TODO: sealed interface par unificar estados con "BookingFlowSheetContent"
// pasar parametros para los string que cambian
// hacer agrupacion estable de los nullables @inmutable data class...

@Composable
fun MyBookingsFlowSheetContent(
    state: MyBookingFlowState,
    court: CourtBooking,
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
            MyBookingFlowState.Hidden -> Unit

            //TODO: hardcode
            is MyBookingFlowState.Confirming -> {
                Spacer(Modifier.height(8.dp))
                Text("Pista: $court.code")
                Text("Fecha: ${court.date}")
                Text("Hora: $court")
                Spacer(Modifier.height(16.dp))
                Row {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.booking_action_cancel))
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onConfirm,
                    ) { Text(stringResource(R.string.booking_action_confirm)) }
                }
            }

            is MyBookingFlowState.Submitting -> {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text("Modificando...")
            }

            is MyBookingFlowState.Failed -> {
                Text(
                    "No se pudo modificar",
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
                    Button(onClick = onRetry) {
                        Text(stringResource(R.string.booking_action_retry))
                    }
                }
            }

            is MyBookingFlowState.Succeeded -> {
                Text(
                    "Confirmada la modificación",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
