package com.example.courtgate.ui.presentation.signup

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
import com.example.courtgate.ui.presentation.signup.components.SignUpForm

@Composable
fun SignUpScreen(
    navigateToHome: () -> Unit,
    navigateToSignIn: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState()

    LaunchedEffect(state.value.isSignedUpIn) {
        if (state.value.isSignedUpIn) {
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

            SignUpForm(
                navigateToSignIn = navigateToSignIn,
                onEmailChange = { viewModel.onEmailChange(it) },
                onPasswordChange = { viewModel.onPasswordChange(it) },
                fetchSignUp = { viewModel.signUp() },
                state = state.value,
            )
        }
    }
}