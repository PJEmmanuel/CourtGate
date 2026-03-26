package com.example.courtgate.ui.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.ResultCourt
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.domain.models.LastResult
import com.example.courtgate.usecases.home.GetLastResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getLastResultUseCase: GetLastResultUseCase,
) : ViewModel() {

    val state: StateFlow<ResultCourt<List<LastResult>>> = getLastResultUseCase.invoke()
        .map<List<LastResult>, ResultCourt<List<LastResult>>> { ResultCourt.Success(it) }
        .catch { e ->
            val domainError = when (e) {
                is DomainException -> e.error
                else -> DomainError.Local.UnknownLocalError
            }
            emit(ResultCourt.Error(domainError))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ResultCourt.Loading
        )
}
