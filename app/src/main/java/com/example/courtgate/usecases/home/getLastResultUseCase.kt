package com.example.courtgate.usecases.home

import com.example.courtgate.home.domain.models.LastResult
import com.example.courtgate.home.domain.repository.HomeRepository

class GetLastResultUseCase(private val homeRepository: HomeRepository) {
    suspend operator fun invoke(): List<LastResult> {
        return homeRepository.getLastResult()
    }
}