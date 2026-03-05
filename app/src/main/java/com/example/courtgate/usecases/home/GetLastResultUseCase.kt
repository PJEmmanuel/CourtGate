package com.example.courtgate.usecases.home

import com.example.courtgate.data.MatchRepository
import com.example.courtgate.domain.models.LastResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLastResultUseCase @Inject constructor(private val matchRepository: MatchRepository) {
    operator fun invoke(): Flow<List<LastResult>> {
        return matchRepository.lastResult
    }
}