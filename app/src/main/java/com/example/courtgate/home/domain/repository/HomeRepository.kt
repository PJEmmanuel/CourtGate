package com.example.courtgate.home.domain.repository

import com.example.courtgate.home.domain.models.CourtList
import com.example.courtgate.home.domain.models.LastResult

interface HomeRepository {

    fun insertLastResult(lastResult: LastResult)
    fun getLastResult(): List<LastResult>

    suspend fun getAllCourtToShow(): Result<List<CourtList>>
}