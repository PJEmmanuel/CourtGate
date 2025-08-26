package com.example.courtgate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.courtgate.authentication.presentation.login.LoginScreen
import com.example.courtgate.authentication.presentation.signup.SignUpScreen
import com.example.courtgate.home.presentation.find.FindCourtScreen
import com.example.courtgate.home.presentation.home.HomeScreen
import com.example.courtgate.home.presentation.core.NavigationBarOnClick
import com.example.courtgate.navigation.screens.FindCourt
import com.example.courtgate.navigation.screens.Home
import com.example.courtgate.navigation.screens.Login
import com.example.courtgate.navigation.screens.SignUp

@Composable
fun NavigationWrapper(navController: NavHostController, startDestination: Any) {

    NavHost(navController = navController, startDestination = startDestination) {

        composable<Login> {
            LoginScreen(
                navigateToSignUp = { navController.navigate(SignUp) },
                navigateToHome = {
                    navController.navigate(Home) {
                        popUpTo<Login> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<SignUp> {
            SignUpScreen(
                navigateToSignIn = {
                    navController.navigate(Login) {
                        popUpTo<Login> { inclusive = true }
                    }
                },
                navigateToHome = {
                    navController.navigate(Home) {
                        popUpTo<Login> { inclusive = true }
                    }
                }
            )
        }
        composable<Home> {
            HomeScreen(
                navigateToFindCourt = { navController.navigate(FindCourt) },
                onNavigate = {
                    when (it) {
                        NavigationBarOnClick.GoToHome -> {
                            navController.navigate(Home) {
                                // Evita duplicados en el back stack
                                popUpTo(Home) { inclusive = false }
                                launchSingleTop = true
                            }
                        }

                        NavigationBarOnClick.GoToBooking -> {
                            navController.navigate(FindCourt) {
                                popUpTo(Home) { inclusive = false }
                                launchSingleTop = true
                            }
                        }

                        NavigationBarOnClick.GoToMatch -> {
                            // Aquí deberías crear otra pantalla tipo segura Match y navegar a ella
                            // navController.navigate(Match)
                        }

                        NavigationBarOnClick.GoToSetting -> {
                            // Aquí deberías crear otra pantalla tipo segura Setting y navegar a ella
                            // navController.navigate(Setting)
                        }
                    }
                },
            )
        }

        composable<FindCourt> {
            FindCourtScreen(
                onNavigate = {
                    when (it) {
                        NavigationBarOnClick.GoToHome -> {
                            navController.navigate(Home) {
                                // Evita duplicados en el back stack
                                popUpTo(Home) { inclusive = false }
                                launchSingleTop = true
                            }
                        }

                        NavigationBarOnClick.GoToBooking -> {
                            navController.navigate(FindCourt) {
                                popUpTo(Home) { inclusive = false }
                                launchSingleTop = true
                            }
                        }

                        NavigationBarOnClick.GoToMatch -> {
                            // Aquí deberías crear otra pantalla tipo segura Match y navegar a ella
                            // navController.navigate(Match)
                        }

                        NavigationBarOnClick.GoToSetting -> {
                            // Aquí deberías crear otra pantalla tipo segura Setting y navegar a ella
                            // navController.navigate(Setting)
                        }
                    }
                }
            )
        }
    }
}
