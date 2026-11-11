package com.example.courtgate.ui.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.ResultCourt
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.LastResult
import com.example.courtgate.ui.presentation.core.loadLatest
import com.example.courtgate.usecases.home.GetLastResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getLastResultUseCase: GetLastResultUseCase,
) : ViewModel() {

    private val retryAttempts = MutableStateFlow(0)

    // Datos solo locales (Room): un fallo afecta a la sección de resultados, no a toda la Home
    val state: StateFlow<ResultCourt<List<LastResult>>> =
        retryAttempts.loadLatest(DomainError.Local.UnknownLocalError) {
            getLastResultUseCase.invoke()
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ResultCourt.Loading
            )

    fun onRetry() {
        retryAttempts.update { it + 1 }
    }
}
