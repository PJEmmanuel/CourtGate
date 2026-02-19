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
import com.example.courtgate.ui.presentation.login.LoginEvent
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
    fetchLogin: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier, contentAlignment = Alignment.Center) {

        Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Log in with email",//TODO harscoded
                style = MaterialTheme.typography.labelLarge
            )
            CourtTextField(
                value = state.email,
                onValueChange = { onEmailChange(it) },
                placeholder = "Email",//TODO harscoded
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
                onValueChange = { onPasswordChange(it) },
                contentDescription = "Enter password",//TODO harscoded
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onAny = {
                    focusManager.clearFocus()
                    fetchLogin()
                }),
                errorMessage = state.passwordError,
                isEnabled = !state.isLoading,
                modifier = Modifier
            )

            CourtButton(
                text = "Login",//TODO harscoded
                modifier = Modifier.fillMaxWidth(),
                isEnabled = !state.isLoading,
                shape = ShapeDefaults.Small
            ) {
                fetchLogin()
            }

            Spacer(Modifier.padding(4.dp))

            TextButton(
                onClick = {} //TODO: Terminar boton Forgot pass
            ) {
                Text("Forgot Password?", textDecoration = TextDecoration.Underline)//TODO harscoded
            }

            TextButton(
                onClick = { navigateToSignUp() }
            ) {
                Text(buildAnnotatedString {
                    append("Don’t have an account? ")//TODO harscoded
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Sign up")
                    }
                })
            }
        }
        if (state.isLoading) { //TODO: Mirar como imprementar cicurlar en boton!
            CircularProgressIndicator()
        }
    }
}