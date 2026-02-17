package com.example.courtgate.usecases.booking

import com.example.courtgate.data.HomeRepository
import com.example.courtgate.domain.models.CourtList

class GetCourtSelectedByCode(private val repository: HomeRepository) {
    suspend fun invoke(code: String): Result<CourtList> {
        return repository.getCourtSelectedByCode(code)
    }
}