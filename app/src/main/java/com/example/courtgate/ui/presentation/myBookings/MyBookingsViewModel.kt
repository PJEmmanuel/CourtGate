package com.example.courtgate.ui.presentation.myBookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.ResultCourt
import com.example.courtgate.ResultManage
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.usecases.booking.DeleteMyBookingsUseCase
import com.example.courtgate.usecases.booking.GetMyBookingsUseCase
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
class MyBookingsViewModel @Inject constructor(
    getMyBookingsUseCase: GetMyBookingsUseCase,
    val deleteMyBookingsUseCase: DeleteMyBookingsUseCase
) : ViewModel() {
//TODO 1 Find muestra pistas reservables del pasado
    // 2. Al abrir el sheet, la info sale cruda al usuario

    private val sheetStateFlow = MutableStateFlow<MyBookingFlowState>(MyBookingFlowState.Hidden)

    val state: StateFlow<ResultCourt<MyBookingsState>> =
        combine(
            getMyBookingsUseCase.invoke(),
            flow2 = sheetStateFlow
        ) { myBookings, uiSheet ->
            MyBookingsState(
                myBookingList = myBookings,
                myBookingFlowState = uiSheet
            )
        }.map<MyBookingsState, ResultCourt<MyBookingsState>> { ResultCourt.Success(it) }
            .catch { e -> emit(ResultCourt.Error(mapError(e))) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ResultCourt.Loading
            )

    fun onBookClicked(booking: CourtBooking) {
        if (sheetStateFlow.value !is MyBookingFlowState.Hidden) return
        sheetStateFlow.value = MyBookingFlowState.Confirming(booking)
    }

    fun onConfirmEditBook() {
        val booking = when (val s = sheetStateFlow.value) {
            is MyBookingFlowState.Confirming -> s.booking
            is MyBookingFlowState.Failed -> s.booking
            else -> return
        }

        sheetStateFlow.value = MyBookingFlowState.Submitting(booking)

        viewModelScope.launch {
            val result = deleteMyBookingsUseCase.invoke(booking.id)
            sheetStateFlow.value = when (result) {
                is ResultManage.Success -> MyBookingFlowState.Succeeded(booking)
                is ResultManage.Failure -> MyBookingFlowState.Failed(booking, result.error)
            }
        }
    }

    fun onDismissSheet() {
        if (sheetStateFlow.value is MyBookingFlowState.Submitting) return
        sheetStateFlow.value = MyBookingFlowState.Hidden
    }

    fun onRetryEdit() {
        if (sheetStateFlow.value !is MyBookingFlowState.Failed) return
        onConfirmEditBook()
    }

    //TODO sacar de aquí
    private fun mapError(e: Throwable): DomainError = when (e) {
        is DomainException -> e.error
        else -> DomainError.Remote.UnknownRemoteError
    }
}
