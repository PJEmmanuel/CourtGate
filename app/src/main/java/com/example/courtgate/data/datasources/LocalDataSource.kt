package com.example.courtgate.data.datasources

import com.example.courtgate.domain.models.LastResult

interface LocalDataSource {

    suspend fun getLastResult(): List<LastResult>
    suspend fun insertLastResult(lastResult: LastResult)
}