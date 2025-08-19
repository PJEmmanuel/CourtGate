package com.example.courtgate.home.presentation.find

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FindViewModel @Inject constructor(): ViewModel(){

    private val _state = MutableStateFlow<FindState>(FindState())
    val state: StateFlow<FindState> = _state.asStateFlow()


    fun selectedDate(){} // obtengo los datos de la fecha, hora..

    fun selectedCourt(){} // Obtengo los datos de la pista selec...

    fun selectedFilterCourt(){} // Obtengo los filtros aplicados( indoor, out, horarios)

    fun fetchCourtList(){} // Obtengo los datos de todas las pistas del server

}