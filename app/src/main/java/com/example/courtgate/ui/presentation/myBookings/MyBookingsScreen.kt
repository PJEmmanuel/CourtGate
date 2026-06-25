package com.example.courtgate.ui.presentation.myBookings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.courtgate.R
import com.example.courtgate.ResultCourt
import com.example.courtgate.ui.presentation.core.CourtNavigationBar
import com.example.courtgate.ui.presentation.core.CourtTopBar
import com.example.courtgate.ui.presentation.core.ErrorScreen
import com.example.courtgate.ui.presentation.core.LoadingScreen
import com.example.courtgate.ui.presentation.core.NavigationBarOnClick
import com.example.courtgate.ui.presentation.myBookings.components.MyBookingsFlowSheetContent
import com.example.courtgate.ui.presentation.myBookings.components.MyBookingsList
import kotlinx.coroutines.delay

private const val SUCCEEDED_DISMISS_DELAY_MS = 2_000L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    viewModel: MyBookingsViewModel = hiltViewModel(),
    onNavigate: (NavigationBarOnClick) -> Unit,
    backToHome: () -> Unit,
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CourtTopBar(
                navIcon = {
                    Image(
                        imageVector = Icons.AutoMirrored.TwoTone.ArrowBack,
                        contentDescription = stringResource(R.string.content_description_back)
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
            when (val s = state) {
                is ResultCourt.Error -> ErrorScreen(s.error) { }
                ResultCourt.Loading -> LoadingScreen()
                is ResultCourt.Success -> {
                    MyBookingsList(
                        modifier = Modifier,
                        courts = s.data.myBookingList,
                        onCourtClick = { viewModel.onBookClicked(it) }
                    )
                }
            }
        }

        //TODO: StateHolder
        val data = (state as? ResultCourt.Success)?.data
        val sheetState = data?.myBookingFlowState

        LaunchedEffect(sheetState) {
            if (sheetState is MyBookingFlowState.Succeeded) {
                delay(SUCCEEDED_DISMISS_DELAY_MS)
                viewModel.onDismissSheet()
                backToHome()
            }
        }

        if (sheetState != null && sheetState !is MyBookingFlowState.Hidden) {
            val modalState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = {
                    (sheetState !is MyBookingFlowState.Succeeded && sheetState !is MyBookingFlowState.Submitting)
                }
            )

            val booking = when (sheetState) {
                is MyBookingFlowState.Confirming -> sheetState.booking
                is MyBookingFlowState.Failed -> sheetState.booking
                MyBookingFlowState.Hidden -> null
                is MyBookingFlowState.Submitting -> sheetState.booking
                is MyBookingFlowState.Succeeded -> sheetState.booking
            }

            ModalBottomSheet(
                onDismissRequest = viewModel::onDismissSheet,
                sheetState = modalState
            ) {
                MyBookingsFlowSheetContent(
                    state = sheetState,
                    court = booking!!, //TODO: Revisar la doble !
                    onConfirm = viewModel::onConfirmEditBook,
                    onRetry = viewModel::onRetryEdit,
                    onDismiss = viewModel::onDismissSheet
                )
            }
        }
    }
}