package com.example.courtgate.usecases.home

import com.example.courtgate.data.MatchRepository
import com.example.courtgate.domain.models.LastResult
import javax.inject.Inject

class InsertLastResultUseCase @Inject constructor(private val matchRepository: MatchRepository) {
    suspend operator fun invoke(lastResult: LastResult) {
        return matchRepository.insertLastResult(lastResult)
    }
}