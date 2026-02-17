package com.example.courtgate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.courtgate.ui.navigation.NavigationWrapper
import com.example.courtgate.ui.navigation.screens.Home
import com.example.courtgate.ui.navigation.screens.Login
import com.example.courtgate.ui.navigation.screens.SignUp
import com.example.courtgate.ui.theme.CourtGateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewmodel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CourtGateTheme {
                val navController = rememberNavController()
                NavigationWrapper(navController, startDestination = getStartDestination())

            }
        }
    }
    //TODO: Acabar cuando toque. Está puesto para poder ejecutar
    fun getStartDestination() : Any{
        if(viewmodel.isLoggedIn){
            return Home
        }
        return if(!viewmodel.isLoggedIn){ Login } else {
            SignUp
        }
    }
}