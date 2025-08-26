package com.example.courtgate.home.domain.usecase

import com.example.courtgate.home.domain.models.CourtList
import com.example.courtgate.home.domain.repository.HomeRepository

class GetAllCourtToShowUseCase(private val repository: HomeRepository) {
    suspend operator fun invoke(): List<CourtList> {
        return repository.getAllCourtToShow()
    }
}