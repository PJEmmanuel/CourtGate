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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.courtgate.R
import com.example.courtgate.ResultCourt
import com.example.courtgate.ui.presentation.core.CourtButton
import com.example.courtgate.ui.presentation.core.CourtNavigationBar
import com.example.courtgate.ui.presentation.core.CourtTopBar
import com.example.courtgate.ui.presentation.core.ErrorScreen
import com.example.courtgate.ui.presentation.core.LoadingScreen
import com.example.courtgate.ui.presentation.core.NavigationBarOnClick
import com.example.courtgate.ui.presentation.home.components.LastResultSection
import com.example.courtgate.ui.presentation.home.components.TitleForm

@Composable
fun HomeScreen(
    onNavigate: (NavigationBarOnClick) -> Unit,
    navigateToFindCourt: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

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

                //TODO: Optimizable con scaffold custom
                when (val s = state) {
                    is ResultCourt.Error -> ErrorScreen(s.exception)
                    ResultCourt.Loading -> LoadingScreen()
                    is ResultCourt.Success -> LastResultSection(
                        lastMatchResult = s.data,
                    )
                }

                CourtButton(
                    onClick = { navigateToFindCourt() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f)
                        .padding(16.dp),
                    shape = ShapeDefaults.Small,
                    text = "Find a Court", //TODO: hard code
                )
            }
        }
    }
}