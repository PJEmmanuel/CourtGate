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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.courtgate.ResultCourt
import com.example.courtgate.ui.presentation.booking.components.BookForm
import com.example.courtgate.ui.presentation.booking.components.BookingFlowSheetContent
import com.example.courtgate.ui.presentation.booking.components.BookingImage
import com.example.courtgate.ui.presentation.core.CourtTopBar
import com.example.courtgate.ui.presentation.core.ErrorScreen
import com.example.courtgate.ui.presentation.core.LoadingScreen
import kotlinx.coroutines.delay

private const val SUCCEEDED_DISMISS_DELAY_MS = 2_000L


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    viewModel: BookingViewModel = hiltViewModel(),
    navigateBackToFindCourt: () -> Unit,
    backToHome: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    // LaunchedEffect(code, date) { viewModel.fetchBookingData(code, date) }

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
            when (val s = state) {
                is ResultCourt.Error -> ErrorScreen(
                    error = s.error,
                    onRetry = { }
                )

                ResultCourt.Loading -> {
                    LoadingScreen()
                    BookForm(
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
                        onBookClicked = {},
                        selectedHour = null
                    )
                }

                is ResultCourt.Success -> {
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
                        modifier = Modifier
                            .padding(
                                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                                bottom = paddingValues.calculateBottomPadding()
                            )
                            .fillMaxWidth()
                            .weight(1f),
                        freeHoursList = s.data.freeHoursOfCourt,
                        court = s.data.requestedCourt,
                        isLoading = false,
                        onHourSelected = { viewModel.onSelectHour(it) },
                        onBookClicked = viewModel::onBookClicked,
                        selectedHour = s.data.selectedHourToBook
                    )
                }
            }
        }
        //TODO: stateholder para simplificarr
        val data = (state as? ResultCourt.Success)?.data
        val sheetState = data?.newBookingFlowState

        LaunchedEffect(sheetState) {
            if (sheetState is NewBookingFlowState.Succeeded) {
                delay(SUCCEEDED_DISMISS_DELAY_MS)
                viewModel.onDismissSheet()
                backToHome()
            }
        }

        if (data != null && sheetState != null && sheetState !is NewBookingFlowState.Hidden) {
            val modalState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { sheetState !is NewBookingFlowState.Submitting &&
                        sheetState !is NewBookingFlowState.Succeeded }
            )
            ModalBottomSheet(
                onDismissRequest = viewModel::onDismissSheet,
                sheetState = modalState,
            ) {
                BookingFlowSheetContent(
                    state = sheetState,
                    court = data.requestedCourt,
                    selectedHour = data.selectedHourToBook,
                    onConfirm = viewModel::onConfirmBooking,
                    onRetry = viewModel::onRetryBooking,
                    onDismiss = viewModel::onDismissSheet,
                    isSelectedHourStillFree = data.isSelectedHourStillFree,
                )
            }
        }
    }
}