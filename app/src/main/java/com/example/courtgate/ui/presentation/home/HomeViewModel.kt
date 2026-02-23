package com.example.courtgate.ui.presentation.home

import androidx.lifecycle.ViewModel
import com.example.courtgate.usecases.home.GetLastResultUseCase
import com.example.courtgate.usecases.home.HomeUsesCases
import com.example.courtgate.usecases.home.InsertLastResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getLastResultUseCase: GetLastResultUseCase,
    private val insertLastResultUseCase: InsertLastResultUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

}

/*
data class SetResult(
    val firstResult: Int,
    val secondResult: Int,
)

data class LastMatchResult(
    val match: List<SetResult>
)*/
