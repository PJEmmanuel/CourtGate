package com.example.courtgate.ui.presentation.core

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.courtgate.R
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.isRetryable

// A partir de aquí se añade el aviso de "sigue fallando" (el botón Reintentar se mantiene)
private const val PERSISTENT_FAILURE_THRESHOLD = 3

// Acción alternativa al reintento: volver, iniciar sesión...
data class ErrorAction(
    @param:StringRes val label: Int,
    val onClick: () -> Unit
)

@Composable
fun ErrorScreen(
    error: DomainError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
    failedAttempts: Int = 1,
    secondaryAction: ErrorAction? = null,
) {
    val icon = when (error) {
        is DomainError.Remote -> Icons.Default.CloudOff
        is DomainError.Court -> Icons.Default.SearchOff
        is DomainError.Local -> Icons.Default.Storage
        else -> Icons.Default.Error
    }

    val message = error.asStringRes()
    val canRetry = error.isRetryable
    val keepsFailing = canRetry && failedAttempts >= PERSISTENT_FAILURE_THRESHOLD

    Box(
        modifier = modifier.padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(message),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            if (keepsFailing) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.error_keeps_failing),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            when {
                canRetry -> {
                    Button(onClick = onRetry) {
                        Text(stringResource(R.string.message_button_error_screen))
                    }
                    secondaryAction?.let { action ->
                        TextButton(onClick = action.onClick) {
                            Text(stringResource(action.label))
                        }
                    }
                }
                // Reintentar no sirve: la acción alternativa pasa a ser la principal
                secondaryAction != null -> Button(onClick = secondaryAction.onClick) {
                    Text(stringResource(secondaryAction.label))
                }
            }
        }
    }
}
