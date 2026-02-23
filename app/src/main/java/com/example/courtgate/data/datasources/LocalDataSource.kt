package com.example.courtgate.data.datasources

import com.example.courtgate.domain.models.LastResult
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    val getLastResult: Flow<List<LastResult>>
    suspend fun insertLastResult(lastResult: LastResult)
}