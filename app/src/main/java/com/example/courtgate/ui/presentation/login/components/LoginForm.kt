package com.example.courtgate.ui.presentation.login.components

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
import androidx.compose.material3.ShapeDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.courtgate.R
import com.example.courtgate.ui.presentation.core.asStringRes
import com.example.courtgate.ui.presentation.login.LoginState
import com.example.courtgate.ui.presentation.core.CourtTextField
import com.example.courtgate.ui.presentation.core.PasswordTextField
import com.example.courtgate.ui.presentation.core.CourtButton

@Composable
fun LoginForm(
    modifier: Modifier = Modifier,
    navigateToSignUp: () -> Unit,
    state: LoginState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    fetchLogin: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier, contentAlignment = Alignment.Center) {

        Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.labelLarge
            )
            CourtTextField(
                value = state.email,
                onValueChange = { onEmailChange(it) },
                placeholder = stringResource(R.string.email_placeholder),
                contentDescription = stringResource(R.string.email_field_description),
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onAny = {
                    focusManager.moveFocus(FocusDirection.Next)
                }),
                errorMessage = state.emailError?.let { stringResource(it.asStringRes()) },
                isEnabled = !state.isLoading,
                modifier = Modifier
            )
            Spacer(Modifier.padding(2.dp))
            PasswordTextField(
                value = state.password,
                onValueChange = { onPasswordChange(it) },
                contentDescription = stringResource(R.string.password_field_description),
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onAny = {
                    focusManager.clearFocus()
                    if (state.isSubmitEnabled) fetchLogin()
                }),
                errorMessage = state.passwordError?.let { stringResource(it.asStringRes()) },
                isEnabled = !state.isLoading,
                modifier = Modifier
            )

            state.authError?.let { error ->
                Text(
                    text = stringResource(error.asStringRes()),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            CourtButton(
                text = stringResource(R.string.login_action),
                modifier = Modifier.fillMaxWidth(),
                isEnabled = state.isSubmitEnabled,
                shape = ShapeDefaults.Small
            ) {
                fetchLogin()
            }

            Spacer(Modifier.padding(4.dp))

            TextButton(
                onClick = { onForgotPassword() },
                enabled = !state.isLoading
            ) {
                Text(stringResource(R.string.login_forgot_password), textDecoration = TextDecoration.Underline)
            }

            TextButton(
                onClick = { navigateToSignUp() }
            ) {
                val noAccount = stringResource(R.string.login_no_account)
                val signUp = stringResource(R.string.login_go_to_sign_up)
                Text(buildAnnotatedString {
                    append(noAccount)
                    append(" ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(signUp)
                    }
                })
            }
        }
        if (state.isLoading) { //TODO: Mirar como imprementar cicurlar en boton!
            CircularProgressIndicator()
        }
    }
}