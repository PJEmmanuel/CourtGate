package com.example.courtgate.ui.presentation.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.courtgate.R
import com.example.courtgate.ui.presentation.core.CourtTextField
import com.example.courtgate.ui.presentation.core.asStringRes
import com.example.courtgate.ui.presentation.login.ResetPasswordState

@Composable
fun ResetPasswordDialog(
    state: ResetPasswordState,
    onEmailChange: (String) -> Unit,
    onSend: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!state.isLoading) onDismiss() },
        title = { Text(stringResource(R.string.reset_password_title)) },
        text = {
            if (state.isSent) {
                Text(stringResource(R.string.reset_password_sent))
            } else {
                Column {
                    Text(stringResource(R.string.reset_password_description))
                    Spacer(Modifier.height(12.dp))
                    CourtTextField(
                        value = state.email,
                        onValueChange = onEmailChange,
                        placeholder = stringResource(R.string.email_placeholder),
                        contentDescription = stringResource(R.string.email_placeholder),
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(
                            autoCorrectEnabled = false,
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(onSend = { onSend() }),
                        errorMessage = state.emailError,
                        isEnabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.error?.let { error ->
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(error.asStringRes()),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (state.isSent) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.reset_password_close))
                }
            } else {
                TextButton(onClick = onSend, enabled = state.isSubmitEnabled) {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text(stringResource(R.string.reset_password_send))
                    }
                }
            }
        },
        dismissButton = {
            if (!state.isSent) {
                TextButton(onClick = onDismiss, enabled = !state.isLoading) {
                    Text(stringResource(R.string.reset_password_cancel))
                }
            }
        }
    )
}
