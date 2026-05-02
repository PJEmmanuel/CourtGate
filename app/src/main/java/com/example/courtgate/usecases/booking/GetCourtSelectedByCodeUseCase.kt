package com.example.courtgate.usecases.booking

import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.domain.models.Court
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCourtSelectedByCodeUseCase @Inject constructor(private val repository: ManageCourtRepository) {
    operator fun invoke(code: String): Flow<Court> =
        repository.getCourtByCode(code)
}
