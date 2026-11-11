package com.example.courtgate.ui.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.courtgate.R
import com.example.courtgate.ResultCourt
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.User
import com.example.courtgate.ui.presentation.core.CourtNavigationBar
import com.example.courtgate.ui.presentation.core.CourtTittle
import com.example.courtgate.ui.presentation.core.CourtTopBar
import com.example.courtgate.ui.presentation.core.ErrorAction
import com.example.courtgate.ui.presentation.core.ErrorScreen
import com.example.courtgate.ui.presentation.core.LoadingScreen
import com.example.courtgate.ui.presentation.core.NavigationBarOnClick
import com.example.courtgate.ui.presentation.core.toBookingDateLabel
import com.example.courtgate.ui.presentation.settings.components.DeleteAccountDialog
import com.example.courtgate.ui.presentation.settings.components.LogoutDialog

@Composable
fun SettingsScreen(
    onNavigate: (NavigationBarOnClick) -> Unit,
    navigateToLogin: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sessionClosed by viewModel.sessionClosed.collectAsStateWithLifecycle()

    LaunchedEffect(sessionClosed) {
        if (sessionClosed) navigateToLogin()
    }

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
        bottomBar = { CourtNavigationBar(onNavigate = onNavigate) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val s = state) {
                is ResultCourt.Error -> ErrorScreen(
                    error = s.error,
                    onRetry = viewModel::onRetry,
                    failedAttempts = s.failedAttempts,
                    secondaryAction = if (s.error == DomainError.Auth.SessionExpired) {
                        ErrorAction(R.string.error_action_login, navigateToLogin)
                    } else null
                )
                ResultCourt.Loading -> LoadingScreen()
                is ResultCourt.Success -> SettingsContent(
                    user = s.data.user,
                    onLogout = viewModel::onLogoutClicked,
                    onDeleteAccount = viewModel::onDeleteAccountClicked
                )
            }
        }
    }

    when (val dialog = (state as? ResultCourt.Success)?.data?.dialog) {
        SettingsDialogState.ConfirmingLogout -> LogoutDialog(
            onConfirm = viewModel::onConfirmLogout,
            onDismiss = viewModel::onDismissDialog
        )

        is SettingsDialogState.DeletingAccount -> DeleteAccountDialog(
            state = dialog,
            onPasswordChange = viewModel::onPasswordChange,
            onConfirm = viewModel::onConfirmDeleteAccount,
            onDismiss = viewModel::onDismissDialog
        )

        SettingsDialogState.Hidden, null -> Unit
    }
}

@Composable
private fun SettingsContent(
    user: User,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CourtTittle(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineSmall
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_account_section),
                    style = MaterialTheme.typography.titleMedium
                )
                AccountField(label = stringResource(R.string.settings_email_label), value = user.email)
                user.createdAt?.let { createdAt ->
                    AccountField(
                        label = stringResource(R.string.settings_member_since_label),
                        value = createdAt.toBookingDateLabel()
                    )
                }
            }
        }

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.settings_logout))
        }

        TextButton(
            onClick = onDeleteAccount,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Text(stringResource(R.string.settings_delete_account))
        }
    }
}

@Composable
private fun AccountField(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
