package com.example.courtgate.usecases.booking

import com.example.courtgate.ResultManage
import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.NewCourtBooking
import javax.inject.Inject

class SetNewBookingUseCase @Inject constructor(val repository: ManageCourtRepository) {
    suspend operator fun invoke(newBooking: NewCourtBooking): ResultManage<Unit, DomainError> {
        return repository.setBooking(newBooking)
    }
}