package com.example.courtgate.home.domain.usecase

import com.example.courtgate.home.domain.models.LastResult
import com.example.courtgate.home.domain.repository.HomeRepository

class InsertLastResultUseCase(private val homeRepository: HomeRepository) {
    operator fun invoke(lastResult: LastResult) {
        return homeRepository.insertLastResult(lastResult)
    }
}