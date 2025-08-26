package com.example.courtgate.home.presentation.find

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.home.domain.usecase.FindUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindViewModel @Inject constructor(
    private val findUseCases: FindUseCases
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<FindState>>(UiState.Idle)
    val state: StateFlow<UiState<FindState>> = _state.asStateFlow()

    init {
        fetchCourtList()
    }

    fun selectedDate() {} // obtengo los datos de la fecha, hora..

    fun selectedCourt() {} // Obtengo los datos de la pista selec...

    fun selectedFilterCourt() {} // Obtengo los filtros aplicados( indoor, out, horarios)

    fun fetchCourtList() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val result = findUseCases.getAllCourtToShowUseCase.invoke()
            _state.value = result.fold(
                onSuccess = {UiState.Success(
                    data = FindState(courtList = it)
                ) },
                onFailure = {UiState.Error(it.message.orEmpty())}
            )
        }
    } // Obtengo los datos de todas las pistas del server

}