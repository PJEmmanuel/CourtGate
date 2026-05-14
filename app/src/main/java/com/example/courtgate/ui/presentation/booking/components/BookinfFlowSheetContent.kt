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

            //TODO: hardcode
            NewBookingFlowState.Confirming -> {
                Text(
                    if (isSelectedHourStillFree) "Confirmar reserva" else "La selección no está disponible, ha sido reservada por otro usuario",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
                Text("Pista: ${court?.name ?: "-"}")
                Text("Hora: ${selectedHour ?: "-"}")
                Text("Precio: ${court?.price ?: "-"}€")
                Spacer(Modifier.height(16.dp))
                Row {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onConfirm,
                        enabled = isSelectedHourStillFree
                    ) { Text("Confirmar") }
                }
            }

            NewBookingFlowState.Submitting -> {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text("Reservando…")
            }

            is NewBookingFlowState.Failed -> {
                Text("No se pudo reservar", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(stringResource(state.error.asStringRes()))
                Spacer(Modifier.height(16.dp))
                Row {
                    TextButton(onClick = onDismiss) { Text("Volver") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = onRetry, enabled = isSelectedHourStillFree) { Text("Reintentar") }
                }
            }

            NewBookingFlowState.Succeeded -> {
                Text("Reserva confirmada", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}