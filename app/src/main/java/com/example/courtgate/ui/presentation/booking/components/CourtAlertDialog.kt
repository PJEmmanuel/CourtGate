/*
package com.example.courtgate.ui.presentation.booking.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ColorLens
import androidx.compose.material.icons.twotone.EuroSymbol
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.courtgate.domain.models.Court

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourtAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    requestedCourt: Court?,
    selectedHourToBook: String
) {
    AlertDialog(
        icon = {
            Icon(Icons.TwoTone.Info, contentDescription = "Icono de Información")
        },
        title = { Text("Confirmar reserva") },

        text = {
            Column {
                Text("¿Deseas reservar la pista a las $selectedHourToBook?")
                Spacer(Modifier.height(8.dp))
                InfoChip(text = "${requestedCourt?.name}", imageVector = Icons.TwoTone.ColorLens)
                InfoChip(text = "${requestedCourt?.located}",imageVector = Icons.TwoTone.LocationOn)
                InfoChip(text = requestedCourt?.price.toString(), Icons.TwoTone.EuroSymbol)
            }
        },
        onDismissRequest = {
            // Se llama cuando el usuario intenta cerrar el diálogo
            // tocando fuera de él o presionando el botón de atrás.
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    // Acción al cancelar o cerrar
                    onDismissRequest()
                }
            ) {
                Text("Cancelar")
            }
        }
    )
}*/
