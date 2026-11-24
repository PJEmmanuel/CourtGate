package com.example.courtgate.ui.presentation.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.ResultCourt
import com.example.courtgate.ResultManage
import com.example.courtgate.di.CourtCode
import com.example.courtgate.di.SelectedDay
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.ui.presentation.core.FlowSheetState
import com.example.courtgate.ui.presentation.core.loadLatest
import com.example.courtgate.usecases.booking.GetCourtSelectedByCodeUseCase
import com.example.courtgate.usecases.booking.GetFreeHoursOnReservedCourtsUseCase
import com.example.courtgate.usecases.booking.SetNewBookingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    getCourtSelectedByCodeUseCase: GetCourtSelectedByCodeUseCase,
    getFreeHoursOnReservedCourtsUseCase: GetFreeHoursOnReservedCourtsUseCase,
    val setNewBookingUseCase: SetNewBookingUseCase,
    @param:CourtCode private val code: String,
    @param:SelectedDay private val selectedDay: Long
) : ViewModel() {

    private val selectedHourFlow = MutableStateFlow<String?>(null)
    private val sheetStateFlow = MutableStateFlow<NewBookingFlowState>(FlowSheetState.Hidden)
    private val retryAttempts = MutableStateFlow(0)

    val state: StateFlow<ResultCourt<BookingState>> =
        retryAttempts.loadLatest(DomainError.Remote.UnknownRemoteError) {
            combine(
                getCourtSelectedByCodeUseCase(code),
                getFreeHoursOnReservedCourtsUseCase(code, selectedDay),
                selectedHourFlow,
                sheetStateFlow,
            ) { serverCourt, serverFreeHours, uiSelectedHour, uiSheet ->
                val steelFree =
                    serverFreeHours.firstOrNull { it.hour == uiSelectedHour }?.isFree ?: false
                BookingState(
                    freeHoursOfCourt = serverFreeHours,
                    requestedCourt = serverCourt,
                    newBookingFlowState = uiSheet,
                    selectedHourToBook = uiSelectedHour,
                    isSelectedHourStillFree = steelFree,
                )
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ResultCourt.Loading
            )

    fun onSelectHour(hour: String) {
        selectedHourFlow.value = hour
    }

    fun onBookClicked() {
        val hour = selectedHourFlow.value ?: return
        if (sheetStateFlow.value !is FlowSheetState.Hidden) return
        sheetStateFlow.value = FlowSheetState.Confirming(hour)
    }

    fun onConfirmBooking() {
        val hour = when (val s = sheetStateFlow.value) {
            is FlowSheetState.Confirming -> s.target
            is FlowSheetState.Failed -> s.target
            else -> return
        }

        sheetStateFlow.value = FlowSheetState.Submitting(hour)

        viewModelScope.launch {
            val result = setNewBookingUseCase(
                code = code,
                date = selectedDay,
                hour = hour
            )
            sheetStateFlow.value = when (result) {
                is ResultManage.Success -> FlowSheetState.Succeeded(hour)
                is ResultManage.Failure -> FlowSheetState.Failed(hour, result.error)
            }
        }
    }

    fun onDismissSheet() {
        val current = sheetStateFlow.value
        if (current is FlowSheetState.Submitting) return // bloquea cierre durante envío
        if (current is FlowSheetState.Succeeded) {
            selectedHourFlow.value = null
        }
        sheetStateFlow.value = FlowSheetState.Hidden
    }

    fun onRetryBooking() {
        if (sheetStateFlow.value !is FlowSheetState.Failed) return
        onConfirmBooking()
    }

    fun onRetryLoad() {
        retryAttempts.update { it + 1 }
    }

}