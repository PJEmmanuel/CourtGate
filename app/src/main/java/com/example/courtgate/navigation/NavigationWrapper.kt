package com.example.courtgate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.courtgate.navigation.screens.Login

@Composable
fun NavigationWrapper(navController: NavHostController) {

    NavHost(navController = navController, startDestination = Login){
        composable<Login> { //LoginScreen  }
    }
}