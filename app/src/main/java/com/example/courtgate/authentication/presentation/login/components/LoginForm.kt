package com.example.courtgate.authentication.presentation.login.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.courtgate.authentication.presentation.login.LoginEvent
import com.example.courtgate.authentication.presentation.login.LoginState
import com.example.courtgate.core.presentation.CourtTextField
import com.example.courtgate.core.presentation.PasswordTextField
import com.example.courtgate.core.presentation.CourtButton

@Composable
fun LoginForm(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit,
    navigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier, contentAlignment = Alignment.Center) {

        Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Log in with email",
                style = MaterialTheme.typography.labelLarge
            )
            CourtTextField(
                value = state.email,
                onValueChange = { onEvent(LoginEvent.EmailChange(it)) },
                placeholder = "Email",
                contentDescription = "Enter email",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onAny = {
                    focusManager.moveFocus(FocusDirection.Next)
                }),
                errorMessage = state.emailError,
                isEnabled = !state.isLoading,
                modifier = Modifier
            )
            Spacer(Modifier.padding(2.dp))
            PasswordTextField(
                value = state.password,
                onValueChange = { onEvent(LoginEvent.PasswordChange(it)) },
                contentDescription = "Enter password",
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onAny = {
                    focusManager.clearFocus()
                    onEvent(LoginEvent.Login)
                }),
                errorMessage = state.passwordError,
                isEnabled = !state.isLoading,
                modifier = Modifier
            )

            CourtButton(
                text = "Login",
                modifier = Modifier.fillMaxWidth(),
                isEnabled = !state.isLoading
            ) {
                onEvent(LoginEvent.Login)
            }

            Spacer(Modifier.padding(4.dp))

            TextButton(
                onClick = {} //TODO: Terminar!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            ) {
                Text("Forgot Password?", textDecoration = TextDecoration.Underline)
            }

            TextButton(
                onClick = { navigateToSignUp() }
            ) {
                Text(buildAnnotatedString {
                    append("Don’t have an account? ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Sign up")
                    }
                })
            }
        }
        if (state.isLoading) {
            CircularProgressIndicator()
        }
    }
}