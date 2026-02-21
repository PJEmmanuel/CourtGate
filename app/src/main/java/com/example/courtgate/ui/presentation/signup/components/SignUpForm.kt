package com.example.courtgate.ui.presentation.signup.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.courtgate.ui.presentation.signup.SignUpState
import com.example.courtgate.ui.presentation.core.CourtButton
import com.example.courtgate.ui.presentation.core.CourtTextField
import com.example.courtgate.ui.presentation.core.PasswordTextField

@Composable
fun SignUpForm(
    modifier: Modifier = Modifier,
    navigateToSignIn: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    fetchSignUp: () -> Unit,
    state: SignUpState,
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
            onValueChange = { onEmailChange(it) },
            placeholder = "Email",//TODO: HardCode
            contentDescription = "Enter Email",//TODO: HardCode
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
            onValueChange = { onPasswordChange(it) },
            contentDescription = "Enter Password",//TODO: HardCode
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
                fetchSignUp()
            })
        )

        CourtButton(
            text = "Create account",//TODO: HardCode
            modifier = Modifier.fillMaxWidth(),
            isEnabled = state.isSubmitEnabled,
            shape = ShapeDefaults.Small
        ) {
            fetchSignUp()
        }
        TextButton(
            onClick = { navigateToSignIn() }
        ) {
            Text(buildAnnotatedString {
                append("Already have an account? ") //TODO: HardCode
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Sign in")//TODO: HardCode
                }
            })
        }
    }
}
