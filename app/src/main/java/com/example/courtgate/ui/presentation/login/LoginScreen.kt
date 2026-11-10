package com.example.courtgate.ui.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.courtgate.R
import com.example.courtgate.ui.presentation.login.components.LoginForm
import com.example.courtgate.ui.presentation.login.components.ResetPasswordDialog

@Composable
fun LoginScreen(
    navigateToHome: () -> Unit,
    navigateToSignUp: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState()

    LaunchedEffect(state.value.isLoggedIn) {
        if (state.value.isLoggedIn) {
            navigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.DarkGray, Color.White
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.court_gate_icon),
                contentDescription = "LogoCourtGate",
                modifier = Modifier,
            )
            LoginForm(
                modifier = Modifier,
                navigateToSignUp = navigateToSignUp,
                onEmailChange = { viewModel.onEmailChange(it) },
                onPasswordChange = { viewModel.onPasswordChange(it) },
                fetchLogin = { viewModel.login() },
                onForgotPassword = { viewModel.onForgotPassword() },
                state = state.value
            )
        }
    }

    state.value.resetPassword?.let { resetState ->
        ResetPasswordDialog(
            state = resetState,
            onEmailChange = { viewModel.onResetEmailChange(it) },
            onSend = { viewModel.sendPasswordReset() },
            onDismiss = { viewModel.onDismissResetPassword() }
        )
    }
}