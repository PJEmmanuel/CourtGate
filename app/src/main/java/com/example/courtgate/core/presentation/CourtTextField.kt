package com.example.courtgate.core.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Password",
    contentDescription: String,
    modifier: Modifier,
    errorMessage: String? = null,
    leadingIcon: ImageVector? = Icons.Default.Lock,
    isEnabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
) {
    CourtTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        contentDescription = contentDescription,
        modifier = modifier,
        errorMessage = errorMessage,
        leadingIcon = leadingIcon,
        isPassword = true,
        isEnabled = isEnabled,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
    )
}

//TODO: Cómo mostrar o dónde se muestra el texto de error
@Composable
fun CourtTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    contentDescription: String,
    modifier: Modifier,
    errorMessage: String? = null,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    isEnabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
) {
    var hidePassword by remember { mutableStateOf(true) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        modifier = modifier
            .fillMaxWidth()
            .semantics { this.contentDescription = contentDescription },
        leadingIcon = leadingIcon?.let { icon ->
            {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = ""
                )
            }
        },
        trailingIcon = {
            if (isPassword) {
                val textSelect = if (hidePassword) "show" else "hide"
                TextButton(
                    onClick = { hidePassword = !hidePassword },
                    enabled = isEnabled
                ) {
                    Text(textSelect, textDecoration = TextDecoration.Underline)
                }
            }
        },
        isError = errorMessage != null,
        enabled = isEnabled,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (isPassword && hidePassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        supportingText = {
            if(errorMessage !=null){
                Text(text = errorMessage)
            }
        }
    )

}