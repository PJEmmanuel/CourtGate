package com.example.courtgate.usecases.home

import com.example.courtgate.data.HomeRepository
import com.example.courtgate.domain.models.LastResult

class InsertLastResultUseCase(private val homeRepository: HomeRepository) {
    operator fun invoke(lastResult: LastResult) {
        return homeRepository.insertLastResult(lastResult)
    }
}