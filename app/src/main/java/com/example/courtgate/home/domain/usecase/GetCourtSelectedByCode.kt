package com.example.courtgate.home.domain.usecase

import com.example.courtgate.home.domain.models.CourtList
import com.example.courtgate.home.domain.repository.HomeRepository

class GetCourtSelectedByCode(private val repository: HomeRepository) {
    suspend fun invoke(code: String): Result<CourtList> {
        return repository.getCourtSelectedByCode(code)
    }
}