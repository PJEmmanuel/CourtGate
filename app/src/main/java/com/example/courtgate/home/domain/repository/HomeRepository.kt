package com.example.courtgate.home.domain.repository

import com.example.courtgate.home.domain.models.LastResult

interface HomeRepository {

    suspend fun insertLastResult(lastResult: LastResult)
    suspend fun getLastResult(): List<LastResult>
}