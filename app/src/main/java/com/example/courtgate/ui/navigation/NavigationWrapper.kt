package com.example.courtgate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
import com.example.courtgate.ui.navigation.screens.Match
import com.example.courtgate.ui.navigation.screens.MyBookings
import com.example.courtgate.ui.navigation.screens.Settings
import com.example.courtgate.ui.navigation.screens.SignUp
import com.example.courtgate.ui.presentation.match.MatchScreen
import com.example.courtgate.ui.presentation.myBookings.MyBookingsScreen
import com.example.courtgate.ui.presentation.settings.SettingsScreen

@Composable
fun NavigationWrapper(navController: NavHostController, startDestination: Any) {

    // Sin sesión no tiene sentido volver atrás: se limpia el back stack
    val navigateToLogin: () -> Unit = {
        navController.navigate(Login) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

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
                onNavigate = navController::navigateFromBottomBar,
            )
        }

        composable<FindCourt> {
            FindCourtScreen(
                onNavigate = navController::navigateFromBottomBar,
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
                navigateBackToFindCourt = {
                    navController.navigate(FindCourt) {
                        popUpTo<FindCourt> { inclusive = true }
                    }
                },
                backToHome = {
                    navController.navigate(Home) {
                        popUpTo<Login> { inclusive = true }
                    }
                }
            )
        }

        composable<MyBookings> {
            MyBookingsScreen(
                onNavigate = navController::navigateFromBottomBar,
                backToHome = {
                    navController.navigate(Home) {
                        popUpTo<Login> { inclusive = true }
                    }
                },
                navigateToLogin = navigateToLogin
            )
        }

        composable<Match> {
            MatchScreen(onNavigate = navController::navigateFromBottomBar)
        }

        composable<Settings> {
            SettingsScreen(
                onNavigate = navController::navigateFromBottomBar,
                navigateToLogin = navigateToLogin
            )
        }

    }
}

private fun NavHostController.navigateFromBottomBar(destination: NavigationBarOnClick) {
    val route: Any = when (destination) {
        NavigationBarOnClick.GoToHome -> Home
        NavigationBarOnClick.GoToMyBookings -> MyBookings
        NavigationBarOnClick.GoToMatch -> Match
        NavigationBarOnClick.GoToSetting -> Settings
    }
    navigate(route) {
        // Evita duplicados en el back stack
        popUpTo(route) { inclusive = false }
        launchSingleTop = true
    }
}
