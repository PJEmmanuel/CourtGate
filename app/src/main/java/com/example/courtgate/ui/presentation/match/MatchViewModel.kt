package com.example.courtgate.ui.presentation.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.ResultCourt
import com.example.courtgate.ResultManage
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.LastResult
import com.example.courtgate.ui.presentation.core.loadLatest
import com.example.courtgate.usecases.home.DeleteLastResultUseCase
import com.example.courtgate.usecases.home.EditLastResultUseCase
import com.example.courtgate.usecases.home.GetLastResultUseCase
import com.example.courtgate.usecases.home.InsertLastResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Dos cifras bastan para cualquier set; el resto lo decide validate()
private const val MAX_SCORE_LENGTH = 2

@HiltViewModel
class MatchViewModel @Inject constructor(
    getLastResultUseCase: GetLastResultUseCase,
    private val insertLastResultUseCase: InsertLastResultUseCase,
    private val editLastResultUseCase: EditLastResultUseCase,
    private val deleteLastResultUseCase: DeleteLastResultUseCase,
) : ViewModel() {

    private val sheetStateFlow = MutableStateFlow<MatchSheetState>(MatchSheetState.Hidden)
    private val retryAttempts = MutableStateFlow(0)

    val state: StateFlow<ResultCourt<MatchState>> =
        retryAttempts.loadLatest(DomainError.Local.UnknownLocalError) {
            combine(getLastResultUseCase.invoke(), sheetStateFlow) { results, sheet ->
                MatchState(results = results, sheet = sheet)
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ResultCourt.Loading
            )

    fun onAddClicked() {
        if (sheetStateFlow.value !is MatchSheetState.Hidden) return
        sheetStateFlow.value = MatchSheetState.Editing(ResultForm())
    }

    fun onEditClicked(result: LastResult) {
        if (sheetStateFlow.value !is MatchSheetState.Hidden) return
        sheetStateFlow.value = MatchSheetState.Editing(result.toForm())
    }

    fun onScoreChange(setIndex: Int, team: Team, value: String) {
        val editing = sheetStateFlow.value as? MatchSheetState.Editing ?: return
        if (editing.isSaving) return
        val score = value.filter { it.isDigit() }.take(MAX_SCORE_LENGTH)
        sheetStateFlow.value = editing.copy(
            form = editing.form.withScore(setIndex, team, score),
            formError = null,
            saveError = null
        )
    }

    fun onSave() {
        val editing = sheetStateFlow.value as? MatchSheetState.Editing ?: return
        if (editing.isSaving) return

        val formError = editing.form.validate()
        if (formError != null) {
            sheetStateFlow.value = editing.copy(formError = formError)
            return
        }

        sheetStateFlow.value = editing.copy(isSaving = true, saveError = null)

        viewModelScope.launch {
            val lastResult = editing.form.toLastResult()
            val result = if (editing.form.isNew) {
                insertLastResultUseCase.invoke(lastResult)
            } else {
                editLastResultUseCase.invoke(lastResult)
            }
            sheetStateFlow.value = when (result) {
                is ResultManage.Success -> MatchSheetState.Hidden
                is ResultManage.Failure -> editing.copy(isSaving = false, saveError = result.error)
            }
        }
    }

    fun onDeleteClicked(result: LastResult) {
        if (sheetStateFlow.value !is MatchSheetState.Hidden) return
        sheetStateFlow.value = MatchSheetState.ConfirmingDelete(result)
    }

    fun onConfirmDelete() {
        val confirming = sheetStateFlow.value as? MatchSheetState.ConfirmingDelete ?: return
        if (confirming.isDeleting) return

        sheetStateFlow.value = confirming.copy(isDeleting = true, error = null)

        viewModelScope.launch {
            sheetStateFlow.value = when (val result = deleteLastResultUseCase.invoke(confirming.result)) {
                is ResultManage.Success -> MatchSheetState.Hidden
                is ResultManage.Failure -> confirming.copy(isDeleting = false, error = result.error)
            }
        }
    }

    fun onDismissSheet() {
        when (val sheet = sheetStateFlow.value) {
            is MatchSheetState.Editing -> if (sheet.isSaving) return
            is MatchSheetState.ConfirmingDelete -> if (sheet.isDeleting) return
            MatchSheetState.Hidden -> return
        }
        sheetStateFlow.value = MatchSheetState.Hidden
    }

    fun onRetry() {
        retryAttempts.update { it + 1 }
    }
}
