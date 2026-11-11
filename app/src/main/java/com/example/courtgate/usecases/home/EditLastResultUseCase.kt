package com.example.courtgate.usecases.home

import com.example.courtgate.ResultManage
import com.example.courtgate.data.MatchRepository
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.LastResult
import javax.inject.Inject

class EditLastResultUseCase @Inject constructor(private val matchRepository: MatchRepository) {
    suspend operator fun invoke(lastResult: LastResult): ResultManage<Unit, DomainError> {
        return matchRepository.editLastResult(lastResult)
    }
}
