package com.example.courtgate.usecases.home

import com.example.courtgate.data.MatchRepository
import com.example.courtgate.domain.models.LastResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLastResultUseCase @Inject constructor(matchRepository: MatchRepository) {
    val getLastResult: Flow<List<LastResult>> = matchRepository.lastResult
}