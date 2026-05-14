package com.example.courtgate.ui.presentation.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.ResultCourt
import com.example.courtgate.ResultManage
import com.example.courtgate.di.CourtCode
import com.example.courtgate.di.SelectedDay
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.usecases.booking.GetCourtSelectedByCodeUseCase
import com.example.courtgate.usecases.booking.GetFreeHoursOnReservedCourtsUseCase
import com.example.courtgate.usecases.booking.SetNewBookingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
    //TODO: Quitar ir a pantalla booking desde el botón reservas(es para gestionar reservas)

    private val selectedHourFlow = MutableStateFlow<String?>(null)
    private val sheetStateFlow = MutableStateFlow<NewBookingFlowState>(NewBookingFlowState.Hidden)

    val state: StateFlow<ResultCourt<BookingState>> =
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
        }.map<BookingState, ResultCourt<BookingState>> { ResultCourt.Success(it) }
            .catch { e -> emit(ResultCourt.Error(mapError(e))) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ResultCourt.Loading
            )

    fun onSelectHour(hour: String) {
        selectedHourFlow.value = hour
    }

    fun onBookClicked() {
        if (selectedHourFlow.value == null) return
        if (sheetStateFlow.value !is NewBookingFlowState.Hidden) return
        sheetStateFlow.value = NewBookingFlowState.Confirming
    }

    fun onConfirmBooking() {
        if (sheetStateFlow.value !is NewBookingFlowState.Confirming
            && sheetStateFlow.value !is NewBookingFlowState.Failed
        ) return
        val hour = selectedHourFlow.value ?: return

        sheetStateFlow.value = NewBookingFlowState.Submitting

        viewModelScope.launch {
            val result = setNewBookingUseCase(
                code = code,
                date = selectedDay,
                hour = hour
            )
            sheetStateFlow.value = when (result) {
                is ResultManage.Success -> NewBookingFlowState.Succeeded
                is ResultManage.Failure -> NewBookingFlowState.Failed(result.error)
            }
        }
    }

    fun onDismissSheet() {
        val current = sheetStateFlow.value
        if (current is NewBookingFlowState.Submitting) return // bloquea cierre durante envío
        if (current is NewBookingFlowState.Succeeded) {
            selectedHourFlow.value = null
        }
        sheetStateFlow.value = NewBookingFlowState.Hidden
    }

    fun onRetryBooking() {
        if (sheetStateFlow.value !is NewBookingFlowState.Failed) return
        onConfirmBooking()
    }

    //TODO sacar de aquí
    private fun mapError(e: Throwable): DomainError = when (e) {
        is DomainException -> e.error
        else -> DomainError.Remote.UnknownRemoteError
    }

}