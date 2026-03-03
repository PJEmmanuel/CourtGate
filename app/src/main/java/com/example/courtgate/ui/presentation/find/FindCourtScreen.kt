package com.example.courtgate.ui.presentation.find

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
import com.example.courtgate.ui.presentation.core.CourtTopBar
import com.example.courtgate.ui.presentation.core.ErrorScreen
import com.example.courtgate.ui.presentation.core.LoadingScreen
import com.example.courtgate.ui.presentation.core.NoConnectionScreen
import com.example.courtgate.ui.presentation.core.CourtNavigationBar
import com.example.courtgate.ui.presentation.core.NavigationBarOnClick
import com.example.courtgate.ui.presentation.find.components.CourtFilter
import com.example.courtgate.ui.presentation.find.components.FindDateSelector
import com.example.courtgate.ui.presentation.find.components.FindTittle
import com.example.courtgate.ui.presentation.find.components.ShowCourt

@Composable
fun FindCourtScreen(
    navigateToBookingScreen: (String, String) -> Unit,
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
                    is FindUiState.Error -> ErrorScreen( error = Throwable("provisional") //TODO: revisar
                    )
                    FindUiState.Idle -> {
                        NoConnectionScreen { } //TODO poner el botón de cargar la lista
                    }

                    is FindUiState.Loading -> {
                        FindDateSelector(
                            mainDate = (state as FindUiState.Loading<FindState>).data.mainDate,
                            onDateClick = { },
                            selectedDate = (state as FindUiState.Loading<FindState>).data.selectedDate
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

                    is FindUiState.Success -> {
                        // Fechas 7 días vista
                        FindDateSelector(
                            mainDate = (state as FindUiState.Success<FindState>).data.mainDate,
                            onDateClick =
                                {
                                    viewModel.selectedDate(it)
                                    //viewModel.fetchCourtList(it)
                                },
                            selectedDate = (state as FindUiState.Success<FindState>).data.selectedDate,
                        )

                        // Filtro (outdoor e indoor, hora)
                        CourtFilter(
                            onLocatedSelected = { viewModel.selectedFilterCourt(it) },
                            selectedHour = (state as FindUiState.Success<FindState>).data.selectedHour,
                            filters = (state as FindUiState.Success<FindState>).data.filterList,
                            onHourSelected = {},
                        )

                        //Resultados de pistas
                        ShowCourt(
                            courtList = (state as FindUiState.Success<FindState>).data.filteredCourtList,
                            onCourtClick = {
                                navigateToBookingScreen(
                                    it.code,
                                    (state as FindUiState.Success<FindState>).data.selectedDate.toString()
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}