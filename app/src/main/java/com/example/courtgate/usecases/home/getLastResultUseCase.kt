package com.example.courtgate.usecases.home

import com.example.courtgate.domain.models.LastResult
import com.example.courtgate.data.HomeRepository

class GetLastResultUseCase(private val homeRepository: HomeRepository) {
    suspend operator fun invoke(): List<LastResult> {
        return homeRepository.getLastResult()
    }
}