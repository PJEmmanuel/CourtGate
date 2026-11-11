package com.example.courtgate.ui.presentation.settings

import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.User

data class SettingsState(
    val user: User,
    val dialog: SettingsDialogState = SettingsDialogState.Hidden
)

sealed interface SettingsDialogState {
    data object Hidden : SettingsDialogState
    data object ConfirmingLogout : SettingsDialogState

    data class DeletingAccount(
        val password: String = "",
        val isDeleting: Boolean = false,
        val error: DomainError? = null
    ) : SettingsDialogState {
        val isSubmitEnabled: Boolean get() = password.isNotBlank() && !isDeleting
    }
}
