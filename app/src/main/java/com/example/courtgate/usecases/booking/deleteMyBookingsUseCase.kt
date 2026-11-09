package com.example.courtgate.usecases.booking

import com.example.courtgate.ResultManage
import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.domain.models.DomainError
import javax.inject.Inject

class DeleteMyBookingsUseCase @Inject constructor(private val repository: ManageCourtRepository) {
    suspend operator fun invoke(docId: String): ResultManage<Unit, DomainError> {
        return repository.deleteMyBookings(docId)
    }
}