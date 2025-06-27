package com.example.courtgate.home.presentation.home

import android.graphics.drawable.shapes.Shape
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.courtgate.R
import com.example.courtgate.core.presentation.CourtTittle
import com.example.courtgate.core.presentation.CourtTopBar
import com.example.courtgate.home.presentation.home.components.CourtNavigationBar
import com.example.courtgate.home.presentation.home.components.LastResult
import com.example.courtgate.home.presentation.home.components.TitleForm

@Composable
fun HomeScreen(
    navigateToFindCourt : () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CourtTopBar(
                navIcon =
                {
                    Image(
                        painter = painterResource(R.drawable.court_gate_icon),
                        contentDescription = "LogoCourtGate"
                    )
                },
            )
        },
        bottomBar = { CourtNavigationBar() }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = it),

            ) {
            TitleForm(modifier = Modifier.padding(16.dp))

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {

                LastResult(
                    lastMatchResult = state.lastMatchResult,
                    modifier = Modifier
                )

                Button(
                    onClick = {navigateToFindCourt()},
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f)
                        .padding(16.dp),
                    shape = ShapeDefaults.Small
                ) {

                    Text(text = "Find a Court", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}