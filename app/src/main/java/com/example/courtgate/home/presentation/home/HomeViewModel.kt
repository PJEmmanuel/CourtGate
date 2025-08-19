package com.example.courtgate.home.presentation.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        fetchMatchResult()
    }

    private fun fetchMatchResult() {
        _state.value = HomeState(lastMatchResult = matchResults)
    }
}

val matchResults = listOf(
    LastMatchResult(
        match = listOf(
            SetResult(firstResult = 3, secondResult = 1),
            SetResult(firstResult = 5, secondResult = 2),
            SetResult(firstResult = 0, secondResult = 4)
        ),
    ),
    LastMatchResult(
        match = listOf(
            SetResult(firstResult = 3, secondResult = 1),
            SetResult(firstResult = 5, secondResult = 2),
            SetResult(firstResult = 0, secondResult = 4)
        ),
    ),
    LastMatchResult(
        match = listOf(
            SetResult(firstResult = 3, secondResult = 1),
            SetResult(firstResult = 5, secondResult = 2),
            SetResult(firstResult = 0, secondResult = 4)
        ),
    ),
    LastMatchResult(
        match = listOf(
            SetResult(firstResult = 3, secondResult = 1),
            SetResult(firstResult = 5, secondResult = 2),
            SetResult(firstResult = 0, secondResult = 4)
        ),
    ),
    LastMatchResult(
        match = listOf(
            SetResult(firstResult = 3, secondResult = 1),
            SetResult(firstResult = 5, secondResult = 2),
            SetResult(firstResult = 0, secondResult = 4)
        ),
    ),
    LastMatchResult(
        match = listOf(
            SetResult(firstResult = 3, secondResult = 1),
            SetResult(firstResult = 5, secondResult = 2),
            SetResult(firstResult = 0, secondResult = 4)
        ),
    )
)

data class SetResult(
    val firstResult: Int,
    val secondResult: Int,
)

data class LastMatchResult(
    val match: List<SetResult>
)