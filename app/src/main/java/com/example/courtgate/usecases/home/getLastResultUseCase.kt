package com.example.courtgate.usecases.home

import com.example.courtgate.data.HomeRepository
import com.example.courtgate.domain.models.LastResult

class GetLastResultUseCase(private val homeRepository: HomeRepository) {
    suspend operator fun invoke(): List<LastResult> {
        return homeRepository.getLastResult()
    }
}