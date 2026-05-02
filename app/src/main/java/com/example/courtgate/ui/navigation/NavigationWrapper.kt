package com.example.courtgate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.courtgate.ui.presentation.login.LoginScreen
import com.example.courtgate.ui.presentation.signup.SignUpScreen
import com.example.courtgate.ui.presentation.booking.BookingScreen
import com.example.courtgate.ui.presentation.find.FindCourtScreen
import com.example.courtgate.ui.presentation.home.HomeScreen
import com.example.courtgate.ui.presentation.core.NavigationBarOnClick
import com.example.courtgate.ui.navigation.screens.Booking
import com.example.courtgate.ui.navigation.screens.FindCourt
import com.example.courtgate.ui.navigation.screens.Home
import com.example.courtgate.ui.navigation.screens.Login
import com.example.courtgate.ui.navigation.screens.SignUp

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
                            // navController.navigate(Match)
                        }

                        NavigationBarOnClick.GoToSetting -> {
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
                            // navController.navigate(Match)
                        }

                        NavigationBarOnClick.GoToSetting -> {
                            // navController.navigate(Setting)
                        }
                    }
                },
                navigateToBookingScreen = { code, zonedDateTime ->
                    val date = zonedDateTime //TODO: Optimizar
                        .toLocalDate()
                        .atStartOfDay(zonedDateTime.zone)
                        .toInstant()
                        .toEpochMilli()

                    navController.navigate(
                        Booking(
                            code = code,
                            date = date
                        )
                    ) {
                        popUpTo(FindCourt) { inclusive = false }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable<Booking> {
            BookingScreen(
                // onNavigate = {},
                navigateBackToFindCourt = {
                    navController.navigate(FindCourt) {
                        popUpTo<FindCourt> { inclusive = true }
                    }
                }

                /*navigateToFindCourt = {
                    navController.navigate(Home) {
                        popUpTo<Login> { inclusive = true }
                    }
                }*/
            )
        }
    }
}
