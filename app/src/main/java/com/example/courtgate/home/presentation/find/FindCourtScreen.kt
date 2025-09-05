package com.example.courtgate.home.presentation.find

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.courtgate.core.presentation.CourtTopBar
import com.example.courtgate.core.presentation.ErrorScreen
import com.example.courtgate.core.presentation.LoadingScreen
import com.example.courtgate.core.presentation.NoConnectionScreen
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
    val state by viewModel.state.collectAsState()

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

    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues)
        ) {
            Column() {

                FindTittle()

                when (state) {
                    is UiState.Error -> ErrorScreen((state as UiState.Error).toString())
                    UiState.Idle -> {
                        NoConnectionScreen { } //TODO poner el botón de cargar la lista
                    }

                    UiState.Loading -> {
                        FindDateSelector(
                            mainDate = ZonedDateTime.now(), //TODO Arreglar
                            onDateClick = { },
                            selectedDate = ZonedDateTime.now(),
                        )

                        // Filtro (outdoor e indoor, hora)
                        /*CourtFilterChips(
                            selectedType = "",
                            onTypeSelected = { },
                            selectedHour = "",
                            onHourSelected = {},
                        )*/
                        LoadingScreen()
                    }

                    is UiState.Success -> { //TODO Elijo pista y recarga el VM entero y rehace la pantalla
                        // Fechas 7 días vista
                        FindDateSelector(
                            mainDate = (state as UiState.Success<FindState>).data.mainDate,
                            onDateClick = { viewModel.selectedDate(it) },
                            selectedDate = (state as UiState.Success<FindState>).data.selectedDate,
                        )

                        // Filtro (outdoor e indoor, hora)
                        CourtFilterChips(
                            selectedType = (state as UiState.Success<FindState>).data.selectedLocate,
                            onLocatedSelected = { viewModel.selectedFilterCourt(it) },
                            selectedHour = (state as UiState.Success<FindState>).data.selectedHour,
                            filters = (state as UiState.Success<FindState>).data.filterList,
                            onHourSelected = {},
                        )

                        //Resultados de pistas
                        ShowCourt(
                            courtList = (state as UiState.Success<FindState>).data.filteredCourtList,
                            onCourtClick = { viewModel.selectedCourt(it) }
                        )
                    }
                }
            }
        }
    }
}