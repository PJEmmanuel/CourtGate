package com.example.courtgate.ui.presentation.find

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.ResultCourt
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.FilterOption
import com.example.courtgate.ui.presentation.core.loadLatest
import com.example.courtgate.usecases.find.GetAllCourtToShowUseCase
import com.example.courtgate.usecases.find.GetFilterOptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class FindViewModel @Inject constructor(
    private val getAllCourtToShowUseCase: GetAllCourtToShowUseCase,
    getFilterOptionUseCase: GetFilterOptionUseCase,
) : ViewModel() {

    private val selectedFilter = MutableStateFlow<String?>(null)
    private val selectedDate = MutableStateFlow(ZonedDateTime.now())
    private val retryAttempts = MutableStateFlow(0)

    val state: StateFlow<ResultCourt<FindState>> =
        combine(
            selectedFilter,
            selectedDate,
            getFilterOptionUseCase.invoke().catch { emit(emptyList()) },
            retryAttempts
        ) { filter, date, options, _ ->
            FindParams(filter, date, options)
        }
            .loadLatest(DomainError.Remote.UnknownRemoteError) { params ->
                getAllCourtToShowUseCase(params.filter, params.date).map { listCourt ->
                    FindState(
                        courts = listCourt,
                        filterList = params.options.map { option -> option.copy(isSelected = option.located == params.filter) },
                        selectedDate = params.date,
                    )
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ResultCourt.Loading
            )

    fun onFilter(filter: String?) {
        val isAlreadySelected = selectedFilter.value == filter
        val newFilterValue = if (isAlreadySelected) null else filter
        selectedFilter.value = newFilterValue
    }

    fun onSelectedDate(date: ZonedDateTime) {
        selectedDate.value = date
    }

    // Antes se reasignaba la misma fecha: StateFlow no emite valores iguales y no se recargaba nada
    fun onRetry() {
        retryAttempts.update { it + 1 }
    }

    private data class FindParams(
        val filter: String?,
        val date: ZonedDateTime,
        val options: List<FilterOption>
    )
}
