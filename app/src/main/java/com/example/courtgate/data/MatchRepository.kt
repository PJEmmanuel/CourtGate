package com.example.courtgate.data

import com.example.courtgate.data.datasources.LocalDataSource
import com.example.courtgate.domain.models.LastResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MatchRepository @Inject constructor(
    private val localDataSource: LocalDataSource
) {

    val lastResult : Flow<List<LastResult>> = localDataSource.getLastResult

    suspend fun insertLastResult(lastResult: LastResult) {
        return localDataSource.insertLastResult(
            lastResult = lastResult
        )
    }
}