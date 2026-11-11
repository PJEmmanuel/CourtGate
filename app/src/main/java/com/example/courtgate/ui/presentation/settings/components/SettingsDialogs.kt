package com.example.courtgate.ui.presentation.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.ui.presentation.core.PasswordTextField
import com.example.courtgate.ui.presentation.core.asStringRes
import com.example.courtgate.ui.presentation.settings.SettingsDialogState

@Composable
fun LogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_logout_title)) },
        text = { Text(stringResource(R.string.settings_logout_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.settings_logout))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.settings_action_cancel))
            }
        }
    )
}

@Composable
fun DeleteAccountDialog(
    state: SettingsDialogState.DeletingAccount,
    onPasswordChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_delete_account_title)) },
        text = {
            Column {
                Text(stringResource(R.string.settings_delete_account_message))
                Spacer(Modifier.height(12.dp))
                PasswordTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    placeholder = stringResource(R.string.settings_password_placeholder),
                    contentDescription = stringResource(R.string.settings_password_placeholder),
                    isEnabled = !state.isDeleting,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { onConfirm() }),
                    modifier = Modifier.fillMaxWidth()
                )
                state.error?.let { error ->
                    // Al reautenticar solo puede fallar la contraseña: el email es el de la sesión
                    val message = if (error == DomainError.Auth.InvalidCredentials) {
                        R.string.settings_wrong_password
                    } else {
                        error.asStringRes()
                    }
                    Text(
                        text = stringResource(message),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = state.isSubmitEnabled) {
                if (state.isDeleting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        text = stringResource(R.string.settings_delete_account_confirm),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !state.isDeleting) {
                Text(stringResource(R.string.settings_action_cancel))
            }
        }
    )
}
