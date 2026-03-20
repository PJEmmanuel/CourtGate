package com.example.courtgate.ui.presentation.find

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.ResultCourt
import com.example.courtgate.domain.models.FilterOption
import com.example.courtgate.usecases.find.GetAllCourtToShowUseCase
import com.example.courtgate.usecases.find.GetFilterOptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class FindViewModel @Inject constructor(
    private val getAllCourtToShowUseCase: GetAllCourtToShowUseCase,
    private val getFilterOptionUseCase: GetFilterOptionUseCase
) : ViewModel() {

    //Inputs
    private val selectedFilter = MutableStateFlow<String?>(null)
    private val selectedDate = MutableStateFlow(ZonedDateTime.now())

    // Carga desde Room
    private val filterOptions = MutableStateFlow<List<FilterOption>>(emptyList())

    init {
        loadFilterOptions()
    }

    private fun loadFilterOptions() {
        viewModelScope.launch {
            val filters = getFilterOptionUseCase()
            filterOptions.value = filters
            Log.i("CourtVM", "filtros: $filters")

        }
    }
    /*El combine debe incluir los filtros para que cuando onFilter cambie la lista de FilterOption
    (su isSelected), la UI reaccione.*/

    //Stream principal
    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<ResultCourt<FindState>> =
        combine(selectedFilter, selectedDate, filterOptions) { filter, date, options ->
            FindParams(filter, date, options)
        }.flatMapLatest { params ->
            getAllCourtToShowUseCase(params.filter, params.date)
                .catch { ResultCourt.Error(it)
                    Log.e("CourtVM", "ErrorVM: $it")
                }
                .map { listCourt ->
                    Log.i("CourtVM", "Courts: $listCourt")
                    ResultCourt.Success(
                        FindState(
                            courts = listCourt,
                            filterList = params.options,
                            selectedDate = params.date,
                        )
                    )
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ResultCourt.Loading
        )

    //TODO: optimizable?
    fun onFilter(filter: String?) {
        val isAlreadySelected = selectedFilter.value == filter
        val newFilterValue = if (isAlreadySelected) null else filter
        selectedFilter.value = newFilterValue

        filterOptions.value = filterOptions.value.map { option ->
            option.copy(isSelected = option.located == newFilterValue)
        }
    }

    fun onSelectedDate(date: ZonedDateTime) {
        selectedDate.value = date
    }

    private data class FindParams(
        val filter: String?,
        val date: ZonedDateTime,
        val options: List<FilterOption>
    )
}