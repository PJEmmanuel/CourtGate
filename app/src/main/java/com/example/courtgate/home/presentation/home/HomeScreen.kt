package com.example.courtgate.home.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.courtgate.R
import com.example.courtgate.core.presentation.CourtTittle
import com.example.courtgate.core.presentation.CourtTopBar
import com.example.courtgate.home.presentation.home.components.result.LastResult

@Composable
fun HomeScreen() {


    Scaffold(
        topBar = {
            CourtTopBar(
                navIcon = {
                    Image(
                        painter = painterResource(R.drawable.court_gate_icon),
                        contentDescription = "LogoCourtGate"
                    )
                }
            )
        }
    ) {
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
                .padding(it)
        ) {
            Column() {

                CourtTittle(text = "Welcome Back")
                LastResult(
                    firstResult = TODO(),
                    secondResult = TODO()
                )

            }
        }
    }

}