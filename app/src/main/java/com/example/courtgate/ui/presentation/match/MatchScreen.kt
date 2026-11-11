package com.example.courtgate.ui.presentation.match

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.courtgate.R
import com.example.courtgate.ResultCourt
import com.example.courtgate.ui.presentation.core.CourtNavigationBar
import com.example.courtgate.ui.presentation.core.CourtTittle
import com.example.courtgate.ui.presentation.core.CourtTopBar
import com.example.courtgate.ui.presentation.core.ErrorScreen
import com.example.courtgate.ui.presentation.core.LoadingScreen
import com.example.courtgate.ui.presentation.core.NavigationBarOnClick
import com.example.courtgate.ui.presentation.match.components.DeleteResultDialog
import com.example.courtgate.ui.presentation.match.components.MatchResultList
import com.example.courtgate.ui.presentation.match.components.ResultFormSheetContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchScreen(
    onNavigate: (NavigationBarOnClick) -> Unit,
    viewModel: MatchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val data = (state as? ResultCourt.Success)?.data

    Scaffold(
        topBar = {
            CourtTopBar(
                navIcon = {
                    Image(
                        painter = painterResource(R.drawable.court_gate_icon),
                        contentDescription = null
                    )
                },
            )
        },
        bottomBar = { CourtNavigationBar(onNavigate = onNavigate) },
        floatingActionButton = {
            // Sin datos cargados no hay dónde añadir el resultado
            if (data != null) {
                FloatingActionButton(onClick = viewModel::onAddClicked) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.match_add_result)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CourtTittle(
                text = stringResource(R.string.match_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
            when (val s = state) {
                is ResultCourt.Error -> ErrorScreen(
                    error = s.error,
                    onRetry = viewModel::onRetry,
                    failedAttempts = s.failedAttempts
                )
                ResultCourt.Loading -> LoadingScreen()
                is ResultCourt.Success -> MatchResultList(
                    results = s.data.results,
                    onEdit = viewModel::onEditClicked,
                    onDelete = viewModel::onDeleteClicked
                )
            }
        }
    }

    when (val sheet = data?.sheet) {
        is MatchSheetState.Editing -> {
            val modalState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { !sheet.isSaving }
            )
            ModalBottomSheet(
                onDismissRequest = viewModel::onDismissSheet,
                sheetState = modalState
            ) {
                ResultFormSheetContent(
                    state = sheet,
                    onScoreChange = viewModel::onScoreChange,
                    onSave = viewModel::onSave,
                    onDismiss = viewModel::onDismissSheet
                )
            }
        }

        is MatchSheetState.ConfirmingDelete -> DeleteResultDialog(
            state = sheet,
            onConfirm = viewModel::onConfirmDelete,
            onDismiss = viewModel::onDismissSheet
        )

        MatchSheetState.Hidden, null -> Unit
    }
}
