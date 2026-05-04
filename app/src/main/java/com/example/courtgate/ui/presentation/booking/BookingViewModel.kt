/*package com.example.courtgate.ui.presentation.booking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.domain.models.FreeHoursOfCourt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingUseCases: BookingUseCases
) : ViewModel() {

    private val _state = MutableStateFlow<BookingUiState<BookingState>>(BookingUiState.Idle)
    val state: StateFlow<BookingUiState<BookingState>> = _state.asStateFlow()

    fun fetchBookingData(code: String, date: String) {
        viewModelScope.launch {
            _state.value = BookingUiState.Loading

            val courtResult = bookingUseCases.getCourtSelectedByCode.invoke(code)
            val hoursResult = bookingUseCases.getFreeHoursOnReservedCourts.invoke(code, date)
            _state.value = courtResult.fold(
                onSuccess = { court ->
                    hoursResult.fold(
                        onSuccess = { hours ->
                            Log.i("bookingVM", hours.toString())
                            BookingUiState.Success(
                                data = BookingState(
                                    code = code,
                                    requestedCourt = court,
                                    freeHoursOfCourt = hours
                                )
                            )
                        },
                        onFailure = { BookingUiState.Error(it.message.orEmpty()) }
                    )
                },
                onFailure = { BookingUiState.Error(it.message.orEmpty()) }
            )
        }
    }

    fun selectedHour(selectedHour: String?) {
        _state.update { currentUiState ->
            if (currentUiState is BookingUiState.Success) {

                val oldData = currentUiState.data

                val updateSelectedHour = onSelectedHourToReserve(
                    listFreeHour = oldData.freeHoursOfCourt,
                    selectedHour = selectedHour
                )
                currentUiState.copy(
                    data = oldData.copy(
                        freeHoursOfCourt = updateSelectedHour,
                        selectedHourToBook = selectedHour ?: "00:00"
                    )
                )
            } else currentUiState
        }
    }

    fun activateAlertDialog() {
        _state.update { currentUiState ->
            if (currentUiState is BookingUiState.Success) {
                val olData = currentUiState.data
                currentUiState.copy(data = olData.copy(showConfirmationDialog = true))
            } else currentUiState
        }
    }

    fun reserveCourt() {}// mandar a fire

    fun dismissDialog() {
        _state.update { currentUiState ->
            if (currentUiState is BookingUiState.Success) {
                val olData = currentUiState.data
                currentUiState.copy(data = olData.copy(showConfirmationDialog = false))
            } else currentUiState
        }
    }//false al dialog

    fun confirmDialog() {} // meter el reserveCourt

    //Activa la hora elegida
    private fun onSelectedHourToReserve(
        listFreeHour: List<FreeHoursOfCourt>,
        selectedHour: String?
    ): List<FreeHoursOfCourt> {
        val options = mutableListOf<FreeHoursOfCourt>()
        options.addAll(
            listFreeHour.map {
                FreeHoursOfCourt(
                    code = it.code,
                    hour = it.hour,
                    isFree = it.isFree,
                    isSelected = selectedHour == it.hour && !it.isSelected
                )
            }
        )
        return options
    }
}*/