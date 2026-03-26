package com.example.courtgate.ui.presentation.booking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.courtgate.R
import com.example.courtgate.ui.presentation.core.CourtTopBar
import com.example.courtgate.ui.presentation.core.ErrorScreen
import com.example.courtgate.ui.presentation.core.LoadingScreen
import com.example.courtgate.ui.presentation.core.NoConnectionScreen
import com.example.courtgate.ui.presentation.booking.components.BookForm
import com.example.courtgate.ui.presentation.booking.components.BookingImage
import com.example.courtgate.ui.presentation.booking.components.CourtAlertDialog

@Composable
fun BookingScreen(
    code: String,
    date: String,
    //onNavigate: (NavigationBarOnClick) -> Unit, // NO HAY bottonBar
    navigateBackToFindCourt: () -> Unit,
//    viewModel: BookingViewModel = hiltViewModel()
) {
   /* val state by viewModel.state.collectAsState()

    //TODO: 1- el popUp
    // 2- llevar la fecha al Alert
    // 3- terminar las llamadas de lambdas
    // 4- Si no hay hora seleccionada, boton Book disable
    // repaso de todo para terminar de ajustar y rematar.

    LaunchedEffect(code, date) { viewModel.fetchBookingData(code, date) }

    Scaffold(
        topBar = {
            CourtTopBar(
                navIcon = {
                    Image(
                        imageVector = Icons.AutoMirrored.TwoTone.ArrowBack,
                        contentDescription = "Go Back",
                        modifier = Modifier.clickable { navigateBackToFindCourt() }
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            when (state) {
                is BookingUiState.Error -> {
                    ErrorScreen(
                        error = Throwable("provisional") //TODO: revisar 
                    )
                }

                BookingUiState.Idle -> {
                    NoConnectionScreen(
                        onRetry = { viewModel.fetchBookingData(code, date) }
                    ) //TODO poner el botón de cargar la lista
                }

                is BookingUiState.Loading -> {
                    LoadingScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.6f)
                    )
                    BookForm(
                        timeOffer = emptyList(), //TODO: Qué le pongo para que respete los espacios?
                        isLoading = true, // para el indicador de carga en el button
                        modifier = Modifier
                            .padding(
                                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                                bottom = paddingValues.calculateBottomPadding()
                            )
                            .fillMaxWidth()
                            .weight(1f),
                        court = null,
                        freeHoursList = emptyList(), //TODO: Qué le pongo para que respete los espacios?
                        onHourSelected = {},
                        onActivateAlertDialog = {}
                    )
                }

                is BookingUiState.Success -> {
                    Box {
                        BookingImage(
                            imageUrl = painterResource(R.drawable.green_court),
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.6f)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp) // altura del degradado
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                )
                        )
                    }
                    BookForm(
                        timeOffer = (state as BookingUiState.Success<BookingState>).data.timeOffer,
                        court = (state as BookingUiState.Success<BookingState>).data.requestedCourt,
                        modifier = Modifier
                            .padding(
                                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                                bottom = paddingValues.calculateBottomPadding()
                            )
                            .fillMaxWidth()
                            .weight(1f),
                        freeHoursList = (state as BookingUiState.Success<BookingState>).data.freeHoursOfCourt,
                        onHourSelected = { viewModel.selectedHour(it) },
                        onActivateAlertDialog = { viewModel.activateAlertDialog() }
                    )
                    if ((state as BookingUiState.Success<BookingState>).data.showConfirmationDialog) {
                        CourtAlertDialog(
                            onDismissRequest = { viewModel.dismissDialog() },
                            onConfirmation = { viewModel.reserveCourt() },
                            requestedCourt = (state as BookingUiState.Success<BookingState>).data.requestedCourt,
                            selectedHourToBook = (state as BookingUiState.Success<BookingState>).data.selectedHourToBook,
                        )
                    }
                }
            }
        }
    }*/
}

/*@Preview
@Composable
fun PreviewBooking() {
    CourtGateTheme {
        BookingScreen(
            code = "e",
            onNavigate = {}
        )
    }
}*/