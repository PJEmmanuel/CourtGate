package com.example.courtgate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.courtgate.authentication.presentation.login.LoginScreen
import com.example.courtgate.authentication.presentation.signup.SignUpScreen
import com.example.courtgate.navigation.screens.Login
import com.example.courtgate.navigation.screens.SignUp

@Composable
fun NavigationWrapper(navController: NavHostController) {

    NavHost(navController = navController, startDestination = Login) {

        composable<Login> {
            LoginScreen(
                navigateToSignUp = { navController.navigate(SignUp) }
            )
        }

        composable<SignUp> {
            SignUpScreen(
                navigateToSignIn = {
                    navController.navigate(Login) {
                        popUpTo<Login> { inclusive = true }
                    }
                },
            )
        }
    }

}
