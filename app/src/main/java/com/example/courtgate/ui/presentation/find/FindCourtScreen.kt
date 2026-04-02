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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.courtgate.R
import com.example.courtgate.ResultCourt
import com.example.courtgate.ui.presentation.core.CourtNavigationBar
import com.example.courtgate.ui.presentation.core.CourtTopBar
import com.example.courtgate.ui.presentation.core.ErrorScreen
import com.example.courtgate.ui.presentation.core.LoadingScreen
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

    val findStateScreen = rememberFindStateScreen(
        state = state,
        onSelectedDate = { viewModel.onSelectedDate(it) }
    )

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
            Column() {

                FindTittle()
                FindDateSelector(
                    dateRange = findStateScreen.datesRange,
                    selectedDate = findStateScreen.selectedDate,
                    onDateClick = { findStateScreen.onSelectedDate(it) }
                )

                when (val s = state) {
                    is ResultCourt.Error -> ErrorScreen(
                        error = s.error,
                        onRetry = { viewModel.onRetry(findStateScreen.selectedDate) }
                    )

                    ResultCourt.Loading -> LoadingScreen()
                    is ResultCourt.Success -> {
                        CourtFilter(
                            onLocatedSelected = { viewModel.onFilter(it) },
                            filters = s.data.filterList,
                        )
                        ShowCourt(
                            courts = s.data.courts,
                            onCourtClick = {
                                navigateToBookingScreen(
                                    it.code,
                                    s.data.selectedDate.toString()
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}
