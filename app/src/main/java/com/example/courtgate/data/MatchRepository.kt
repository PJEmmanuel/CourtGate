package com.example.courtgate.data

import com.example.courtgate.data.datasources.LocalDataSource
import com.example.courtgate.domain.models.LastResult
import javax.inject.Inject

class MatchRepository @Inject constructor(
    private val localDataSource: LocalDataSource
) {
    suspend fun insertLastResult(lastResult: LastResult) {
        return localDataSource.insertLastResult(
            lastResult = lastResult
        )
    }

    suspend fun getLastResult(): List<LastResult> {
        return localDataSource.getLastResult()
    }
}