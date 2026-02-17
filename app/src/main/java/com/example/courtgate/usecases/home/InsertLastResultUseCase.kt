package com.example.courtgate.usecases.home

import com.example.courtgate.domain.models.LastResult
import com.example.courtgate.data.HomeRepository

class InsertLastResultUseCase(private val homeRepository: HomeRepository) {
    operator fun invoke(lastResult: LastResult) {
        return homeRepository.insertLastResult(lastResult)
    }
}