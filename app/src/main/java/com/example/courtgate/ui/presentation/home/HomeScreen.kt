package com.example.courtgate.ui.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.courtgate.R
import com.example.courtgate.ui.presentation.core.CourtButton
import com.example.courtgate.ui.presentation.core.CourtTopBar
import com.example.courtgate.ui.presentation.core.CourtNavigationBar
import com.example.courtgate.ui.presentation.core.NavigationBarOnClick
import com.example.courtgate.ui.presentation.home.components.LastResult
import com.example.courtgate.ui.presentation.home.components.TitleForm

@Composable
fun HomeScreen(
    onNavigate: (NavigationBarOnClick) -> Unit,
    navigateToFindCourt: () -> Unit,
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
        bottomBar = {
            CourtNavigationBar(
                onNavigate = onNavigate,
            )
        }
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

                CourtButton(
                    onClick = { navigateToFindCourt() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f)
                        .padding(16.dp),
                    shape = ShapeDefaults.Small,
                    text = "Find a Court",
                )
            }
        }
    }
}