package com.example.courtgate.home.presentation.find

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

   /* private val _state = MutableStateFlow<FindState>(FindState())
    val state: StateFlow<FindState> = _state.asStateFlow()*/
    var state by mutableStateOf(FindState())
    private set


    fun selectedDate() {} // obtengo los datos de la fecha, hora..

    fun selectedCourt() {} // Obtengo los datos de la pista selec...

    fun selectedFilterCourt() {} // Obtengo los filtros aplicados( indoor, out, horarios)

    fun fetchCourtList() {
        viewModelScope.launch {
            val getCourt = findUseCases.getAllCourtToShowUseCase
            _state.value.courtList = getCourt
        }

    } // Obtengo los datos de todas las pistas del server

}