package com.example.courtgate.ui.presentation.match

import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.LastResult

data class MatchState(
    val results: List<LastResult> = emptyList(),
    val sheet: MatchSheetState = MatchSheetState.Hidden
)

sealed interface MatchSheetState {
    data object Hidden : MatchSheetState

    // formError: el formulario no es válido. saveError: Room ha fallado al guardar
    data class Editing(
        val form: ResultForm,
        val formError: ResultFormError? = null,
        val isSaving: Boolean = false,
        val saveError: DomainError? = null
    ) : MatchSheetState

    data class ConfirmingDelete(
        val result: LastResult,
        val isDeleting: Boolean = false,
        val error: DomainError? = null
    ) : MatchSheetState
}
