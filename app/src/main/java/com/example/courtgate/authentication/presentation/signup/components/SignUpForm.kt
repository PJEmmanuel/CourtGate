package com.example.courtgate.authentication.presentation.signup.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.courtgate.authentication.presentation.signup.SignUpEvent
import com.example.courtgate.authentication.presentation.signup.SignUpState
import com.example.courtgate.core.presentation.CourtButton
import com.example.courtgate.core.presentation.CourtTextField
import com.example.courtgate.core.presentation.PasswordTextField
import com.example.courtgate.ui.theme.bodyFontFamily
import com.example.courtgate.ui.theme.displayFontFamily

@Composable
fun SignUpForm(
    navigateToSignIn: () -> Unit,
    onEvent: (SignUpEvent) -> Unit,
    state: SignUpState,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create your account",
            style = MaterialTheme.typography.labelLarge
        )
        CourtTextField(
            value = state.email,
            onValueChange = { onEvent(SignUpEvent.EmailChange(it)) },
            placeholder = "Email",
            contentDescription = "Enter Email",
            modifier = Modifier,
            errorMessage = state.emailError,
            leadingIcon = Icons.Default.Email,
            isPassword = false,
            isEnabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onAny = {
                focusManager.moveFocus(FocusDirection.Next)
            })
        )
        Spacer(Modifier.padding(2.dp))
        PasswordTextField(
            value = state.password,
            onValueChange = { onEvent(SignUpEvent.PasswordChange(it)) },
            contentDescription = "Enter Password",
            modifier = Modifier,
            errorMessage = state.passwordError,
            isEnabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onAny = {
                focusManager.clearFocus()
                onEvent(SignUpEvent.SignUp)
            })
        )
        CourtButton(
            text = "Create account",
            modifier = Modifier.fillMaxWidth(),
            isEnabled = !state.isLoading
        ) {
            onEvent(SignUpEvent.SignUp)
        }
        TextButton(
            onClick = { navigateToSignIn() }
        ) {
            Text(buildAnnotatedString {
                append("Already have an account? ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Sign in")
                }
            })
        }
    }
}
