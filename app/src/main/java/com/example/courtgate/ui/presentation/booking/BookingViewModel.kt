package com.example.courtgate.ui.presentation.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.ResultCourt
import com.example.courtgate.ResultManage
import com.example.courtgate.di.CourtCode
import com.example.courtgate.di.SelectedDay
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.domain.models.NewCourtBooking
import com.example.courtgate.usecases.booking.GetCourtSelectedByCodeUseCase
import com.example.courtgate.usecases.booking.GetFreeHoursOnReservedCourtsUseCase
import com.example.courtgate.usecases.booking.SetNewBookingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    @CourtCode code: String,
    @SelectedDay selectedDay: Long
) : ViewModel() {

    //TODO: Quitar ir a pantalla booking desde el botón reservas(es para gestionar reservas)
    val state: StateFlow<ResultCourt<BookingState>> =
        combine(
            getCourtSelectedByCodeUseCase(code),
            getFreeHoursOnReservedCourtsUseCase(code, selectedDay)
        ) { court, freeHours ->
            BookingState(
                freeHoursOfCourt = freeHours,
                requestedCourt = court,
            )
        }.map<BookingState, ResultCourt<BookingState>> { ResultCourt.Success(it) }
            .catch { e -> emit(ResultCourt.Error(mapError(e))) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ResultCourt.Loading
            )


    //TODO sacar de aquí
    private fun mapError(e: Throwable): DomainError = when (e) {
        is DomainException -> e.error
        else -> DomainError.Remote.UnknownRemoteError
    }

}