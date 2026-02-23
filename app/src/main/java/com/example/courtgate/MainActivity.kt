package com.example.courtgate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.courtgate.ui.navigation.NavigationWrapper
import com.example.courtgate.ui.navigation.screens.Home
import com.example.courtgate.ui.navigation.screens.Login
import com.example.courtgate.ui.presentation.MainViewModel
import com.example.courtgate.ui.theme.CourtGateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CourtGateTheme {
                val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
                val navController = rememberNavController()
                NavigationWrapper(
                    navController = navController,
                    startDestination = if (isLoggedIn) Home else Login
                )
            }
        }
    }
}
