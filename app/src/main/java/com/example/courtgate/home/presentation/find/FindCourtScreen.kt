package com.example.courtgate.home.presentation.find

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.courtgate.core.presentation.CourtTopBar
import com.example.courtgate.home.presentation.core.CourtNavigationBar
import com.example.courtgate.home.presentation.core.NavigationBarOnClick
import com.example.courtgate.home.presentation.find.components.CourtFilterChips
import com.example.courtgate.home.presentation.find.components.FindDateSelector
import com.example.courtgate.home.presentation.find.components.FindTittle
import com.example.courtgate.home.presentation.find.components.ShowCourt
import java.time.ZonedDateTime

@Composable
fun FindCourtScreen(
    onNavigate: (NavigationBarOnClick) -> Unit,
    viewModel: FindViewModel = hiltViewModel()
) {
   //val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CourtTopBar(
                navIcon = {
                    Image(
                        imageVector = Icons.AutoMirrored.TwoTone.ArrowBack,
                        contentDescription = "Go Back"
                    )
                },
            )
        },
        bottomBar = {
            CourtNavigationBar(onNavigate = onNavigate)
        }

    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = it)
        ) {
            Column() {

                FindTittle()

                // Fechas 7 días vista
                FindDateSelector(
                    mainDate = state.mainDate,
                    onDateClick = {},
                )

                // Filtro (outdoor e indoor, hora)
                CourtFilterChips(
                    selectedType = state.selectedType,
                    onTypeSelected = {},
                    selectedHour = state.selectedHour,
                    onHourSelected = {},
                )

                //Resultados de pistas
                ShowCourt(
                    courtList = state.courtList,
                    onCourtClick = {}
                )
            }
        }
    }
}