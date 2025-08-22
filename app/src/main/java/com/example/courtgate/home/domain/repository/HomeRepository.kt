package com.example.courtgate.home.domain.repository

import com.example.courtgate.home.data.local.entity.LastResultEntity
import com.example.courtgate.home.domain.models.CourtList
import com.example.courtgate.home.domain.models.LastResult
import com.example.courtgate.home.domain.usecase.GetAllCourtToShowUseCase
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    fun insertLastResult(lastResult: LastResult)
    fun getLastResult(): List<LastResult>

    suspend fun getAllCourtToShow(): List<CourtList>
}