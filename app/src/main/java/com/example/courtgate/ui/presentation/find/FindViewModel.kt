package com.example.courtgate.ui.presentation.find

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.ResultCourt
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
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
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class FindViewModel @Inject constructor(
    private val getAllCourtToShowUseCase: GetAllCourtToShowUseCase,
    getFilterOptionUseCase: GetFilterOptionUseCase,
) : ViewModel() {

    private val selectedFilter = MutableStateFlow<String?>(null)
    private val selectedDate = MutableStateFlow(ZonedDateTime.now())

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<ResultCourt<FindState>> =
        combine(
            selectedFilter,
            selectedDate,
            getFilterOptionUseCase.invoke().catch { emit(emptyList()) }
        ) { filter, date, options ->
            FindParams(filter, date, options)
        }
            .flatMapLatest { params ->
                getAllCourtToShowUseCase(params.filter, params.date)
                    .map<List<Court>, ResultCourt<FindState>> { listCourt ->
                        ResultCourt.Success(
                            FindState(
                                courts = listCourt,
                                filterList = params.options.map { option -> option.copy(isSelected = option.located == params.filter) },
                                selectedDate = params.date,
                            )
                        )
                    }.catch { e ->
                        val domainError = when (e) {
                            is DomainException -> e.error
                            else -> DomainError.Remote.UnknownRemoteError
                        }
                        emit(ResultCourt.Error(domainError))
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

    fun onRetry(date: ZonedDateTime) {
        selectedDate.value = date
    }

    private data class FindParams(
        val filter: String?,
        val date: ZonedDateTime,
        val options: List<FilterOption>
    )
}
