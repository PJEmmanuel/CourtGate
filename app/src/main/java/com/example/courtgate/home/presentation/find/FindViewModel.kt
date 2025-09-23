package com.example.courtgate.home.presentation.find

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.home.domain.models.CourtList
import com.example.courtgate.home.domain.models.FilterOption
import com.example.courtgate.home.domain.usecase.FindUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class FindViewModel @Inject constructor(
    private val findUseCases: FindUseCases
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<FindState>>(UiState.Idle)
    val state: StateFlow<UiState<FindState>> = _state.asStateFlow()


    init {
        Log.i("checkINIT", _state.value.toString())
        fetchCourtList(date = ZonedDateTime.now())
        Log.i("checkINIT", "Ha recargado el VM")
    }

    fun selectedDate(selectedDate: ZonedDateTime) {
        _state.update { currentUiState ->
            if (currentUiState is UiState.Success) {
                val oldData = currentUiState.data
                currentUiState.copy(data = oldData.copy(selectedDate = selectedDate))
            } else currentUiState
        }
        fetchCourtList(date = selectedDate)//TODO: Va justo en esta linea? es correcta la actualizacion aqui?
    }

    //TODO revisar la actualization de datos
    fun selectedCourt(selectedCourt: CourtList) {
    }


    fun selectedFilterCourt(selectedLocate: String?) {
        _state.update { currentUiState ->
            if (currentUiState is UiState.Success) {

                val oldData = currentUiState.data

                //Para filtro seleccionado
                val updatedFilters = onSelectedFilter(
                    listFilter = oldData.filterList,
                    selectedLocate = selectedLocate,
                )
                // Para cambiar el valor cuando se quita el filtro
                val updateSelectedLocate = checkSelectedLocate(
                    currentSelectedLocate = selectedLocate,
                    oldSelectedLocate = oldData.selectedLocate
                )
                // Para lista filtrada
                val updateCourtList = filteredListCourt(
                    mainList = oldData.mainCourtList,
                    selectedLocate = updateSelectedLocate
                )

                currentUiState.copy(
                    data = oldData.copy(
                        filteredCourtList = updateCourtList,
                        selectedLocate = updateSelectedLocate,
                        filterList = updatedFilters
                    )
                )
            } else {
                currentUiState
            }
        }
        Log.i("checkINIT", "Filter $selectedLocate")
        Log.i("checkINIT", "Filter ${_state.value}")
    }

    private fun fetchCourtList(date: ZonedDateTime) {
        viewModelScope.launch {
            when (_state.value) {
                is UiState.Error -> {}
                UiState.Idle -> {
                    _state.value = UiState.Loading(
                        data = FindState()
                    )
                    val result = findUseCases.getAllCourtToShowUseCase.invoke(date = date)
                    _state.value = result.fold(
                        onSuccess = {
                            Log.i("checkINIT", "IdleWhen")
                            UiState.Success(
                                data = FindState(
                                    mainCourtList = it,
                                    filteredCourtList = it,
                                    filterList = fetchFilter(it)
                                )
                            )
                        },
                        onFailure = { UiState.Error(it.message.orEmpty()) }
                    )
                }

                is UiState.Loading -> {}
                is UiState.Success -> {
                    val prevData = (_state.value as? UiState.Success)?.data //TODO: Util????????????????
                    _state.value = UiState.Loading(
                        data = FindState(selectedDate = date)
                    )
                    val fetchData =
                        findUseCases.getAllCourtToShowUseCase.invoke(date = date)
                    _state.update {
                        Log.i("checkINIT", "SuccessWhen")
                        fetchData.fold(
                            onSuccess = {
                                UiState.Success(
                                    data = FindState(
                                        mainCourtList = it,
                                        filteredCourtList = it,
                                        filterList = fetchFilter(it),
                                        selectedDate = date,
                                    )
                                )

                            },
                            onFailure = { UiState.Error(it.message.orEmpty()) }
                        )
                    }
                }
            }
        }
    }


    /*private fun fetchCourtList(date : ZonedDateTime) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val result = findUseCases.getAllCourtToShowUseCase.invoke(date = date)
            _state.value = result.fold(
                onSuccess = {
                    Log.i("checkINIT", "onSuccessSide")
                    UiState.Success(
                        data = FindState(
                            mainCourtList = it,
                            filteredCourtList = it,
                            filterList = fetchFilter(it)
                        )
                    )
                },
                onFailure = { UiState.Error(it.message.orEmpty()) }
            )
        }
    }*/

    // Mapea los tipos de pista directamente
    private fun fetchFilter(mainList: List<CourtList>): List<FilterOption> {
        val options = mutableListOf<FilterOption>()
        // Mapear tipos del backend
        options.addAll(
            mainList.map { located ->
                FilterOption(
                    located = located.located,
                    isSelected = false
                )
            }
        )
        return options
    }


    /***** Filtros *****/
// Activa el filtro elegido
    private fun onSelectedFilter(
        listFilter: List<FilterOption>,
        selectedLocate: String?
    ): List<FilterOption> {
        val options = mutableListOf<FilterOption>()
        // Mapear tipos del backend
        options.addAll(
            listFilter.map {
                FilterOption(
                    located = it.located,
                    isSelected = selectedLocate == it.located && !it.isSelected
                )
            }
        )
        return options
    }

    // Actualiza la lista filtrada
    private fun filteredListCourt(
        mainList: List<CourtList>,
        selectedLocate: String?
    ): List<CourtList> {
        Log.i("checkINIT", "variable de filteredListCourt: $selectedLocate")
        if (selectedLocate.isNullOrEmpty()) {
            return mainList
        }
        return mainList.filter { court ->
            court.located == selectedLocate
        }
    }

    private fun checkSelectedLocate(
        currentSelectedLocate: String?,
        oldSelectedLocate: String?
    ): String? {
        return if (currentSelectedLocate == oldSelectedLocate) null else currentSelectedLocate
    }
}