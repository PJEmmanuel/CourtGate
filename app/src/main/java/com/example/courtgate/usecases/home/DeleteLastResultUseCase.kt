package com.example.courtgate.usecases.home

import com.example.courtgate.data.MatchRepository
import com.example.courtgate.domain.models.LastResult
import javax.inject.Inject

class DeleteLastResultUseCase @Inject constructor(val repository: MatchRepository) {
    suspend operator fun invoke(lastResult: LastResult) {
        return repository.deleteLastResult(lastResult)
    }
}