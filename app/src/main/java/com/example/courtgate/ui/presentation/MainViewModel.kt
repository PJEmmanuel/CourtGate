package com.example.courtgate.ui.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.usecases.authentication.GetUserIdUseCase
import com.example.courtgate.usecases.authentication.ObserveAuthStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getUserIdUseCase: GetUserIdUseCase,
    observeAuthStateUseCase: ObserveAuthStateUseCase
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = observeAuthStateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = getUserIdUseCase() != null
        )
}
