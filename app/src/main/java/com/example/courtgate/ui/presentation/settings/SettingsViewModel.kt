package com.example.courtgate.ui.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.ResultCourt
import com.example.courtgate.ResultManage
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.ui.presentation.core.loadLatest
import com.example.courtgate.usecases.settings.DeleteAccountUseCase
import com.example.courtgate.usecases.settings.GetCurrentUserUseCase
import com.example.courtgate.usecases.settings.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
) : ViewModel() {

    private val dialogStateFlow = MutableStateFlow<SettingsDialogState>(SettingsDialogState.Hidden)
    private val retryAttempts = MutableStateFlow(0)

    // true tras cerrar sesión o borrar la cuenta: la pantalla navega al login
    private val _sessionClosed = MutableStateFlow(false)
    val sessionClosed: StateFlow<Boolean> = _sessionClosed.asStateFlow()

    val state: StateFlow<ResultCourt<SettingsState>> =
        retryAttempts.loadLatest(DomainError.Auth.UnknownAuthError) {
            combine(getCurrentUserUseCase.invoke(), dialogStateFlow) { user, dialog ->
                SettingsState(user = user, dialog = dialog)
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ResultCourt.Loading
            )

    fun onLogoutClicked() {
        if (dialogStateFlow.value !is SettingsDialogState.Hidden) return
        dialogStateFlow.value = SettingsDialogState.ConfirmingLogout
    }

    fun onConfirmLogout() {
        if (dialogStateFlow.value !is SettingsDialogState.ConfirmingLogout) return
        signOutUseCase.invoke()
        dialogStateFlow.value = SettingsDialogState.Hidden
        _sessionClosed.value = true
    }

    fun onDeleteAccountClicked() {
        if (dialogStateFlow.value !is SettingsDialogState.Hidden) return
        dialogStateFlow.value = SettingsDialogState.DeletingAccount()
    }

    fun onPasswordChange(password: String) {
        val dialog = dialogStateFlow.value as? SettingsDialogState.DeletingAccount ?: return
        if (dialog.isDeleting) return
        dialogStateFlow.value = dialog.copy(password = password, error = null)
    }

    fun onConfirmDeleteAccount() {
        val dialog = dialogStateFlow.value as? SettingsDialogState.DeletingAccount ?: return
        if (!dialog.isSubmitEnabled) return

        dialogStateFlow.value = dialog.copy(isDeleting = true, error = null)

        viewModelScope.launch {
            when (val result = deleteAccountUseCase.invoke(dialog.password)) {
                is ResultManage.Success -> {
                    dialogStateFlow.value = SettingsDialogState.Hidden
                    _sessionClosed.value = true
                }
                is ResultManage.Failure -> dialogStateFlow.value =
                    dialog.copy(isDeleting = false, error = result.error)
            }
        }
    }

    fun onDismissDialog() {
        val dialog = dialogStateFlow.value
        if (dialog is SettingsDialogState.DeletingAccount && dialog.isDeleting) return
        dialogStateFlow.value = SettingsDialogState.Hidden
    }

    fun onRetry() {
        retryAttempts.update { it + 1 }
    }
}
