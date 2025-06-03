package com.example.courtgate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.courtgate.navigation.NavigationWrapper
import com.example.courtgate.ui.theme.CourtGateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CourtGateTheme {
                val navController = rememberNavController()
                NavigationWrapper(navController)

            }
        }
    }
}