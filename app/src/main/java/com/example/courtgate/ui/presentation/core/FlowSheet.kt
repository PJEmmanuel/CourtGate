package com.example.courtgate.ui.presentation.core

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.courtgate.R
import com.example.courtgate.domain.models.DomainError

// Estado común de los bottom sheets confirmar → enviar → resultado (nueva reserva y
// cancelar reserva). `target` es sobre qué se actúa: la hora que se reserva o la reserva
// que se cancela; así, al reintentar desde Failed se sabe qué volver a enviar.
sealed interface FlowSheetState<out T> {
    data object Hidden : FlowSheetState<Nothing>
    data class Confirming<T>(val target: T) : FlowSheetState<T>
    data class Submitting<T>(val target: T) : FlowSheetState<T>
    data class Failed<T>(val target: T, val error: DomainError) : FlowSheetState<T>
    data class Succeeded<T>(val target: T) : FlowSheetState<T>
}

val <T> FlowSheetState<T>.targetOrNull: T?
    get() = when (this) {
        FlowSheetState.Hidden -> null
        is FlowSheetState.Confirming -> target
        is FlowSheetState.Submitting -> target
        is FlowSheetState.Failed -> target
        is FlowSheetState.Succeeded -> target
    }

// Mientras se envía no se puede cerrar; tras el éxito se cierra solo
val FlowSheetState<*>.canDismiss: Boolean
    get() = this !is FlowSheetState.Submitting && this !is FlowSheetState.Succeeded

// Los textos que cambian entre un sheet y otro. Volver y Reintentar (en Failed) son comunes.
@Immutable
data class FlowSheetTexts(
    @param:StringRes val confirmTitle: Int,
    @param:StringRes val confirmAction: Int,
    @param:StringRes val confirmDismissAction: Int,
    @param:StringRes val submitting: Int,
    @param:StringRes val failedTitle: Int,
    @param:StringRes val succeededTitle: Int,
)

@Composable
fun FlowSheetContent(
    state: FlowSheetState<*>,
    texts: FlowSheetTexts,
    onConfirm: () -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    isActionEnabled: Boolean = true,
    details: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (state) {
            FlowSheetState.Hidden -> Unit

            is FlowSheetState.Confirming -> {
                Text(
                    stringResource(texts.confirmTitle),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
                details()
                Spacer(Modifier.height(16.dp))
                Row {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(texts.confirmDismissAction))
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onConfirm,
                        enabled = isActionEnabled
                    ) { Text(stringResource(texts.confirmAction)) }
                }
            }

            is FlowSheetState.Submitting -> {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text(stringResource(texts.submitting))
            }

            is FlowSheetState.Failed -> {
                Text(
                    stringResource(texts.failedTitle),
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
                    Button(onClick = onRetry, enabled = isActionEnabled) {
                        Text(stringResource(R.string.booking_action_retry))
                    }
                }
            }

            is FlowSheetState.Succeeded -> {
                Text(
                    stringResource(texts.succeededTitle),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
