package com.example.courtgate.data

import com.example.courtgate.ResultManage
import com.example.courtgate.data.datasources.ResultLocalDataSource
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.LastResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Resultados solo en local (Room) en v1.0: las escrituras pasan directas al data source
class MatchRepository @Inject constructor(
    private val resultLocalDataSource: ResultLocalDataSource
) {

    val lastResult: Flow<List<LastResult>>
        get() = resultLocalDataSource.getLastResult.map { it.filterNotNull() }

    suspend fun insertLastResult(lastResult: LastResult): ResultManage<Unit, DomainError> {
        return resultLocalDataSource.insertLastResult(lastResult)
    }

    suspend fun deleteLastResult(lastResult: LastResult): ResultManage<Unit, DomainError> {
        return resultLocalDataSource.deleteLastResult(lastResult)
    }

    suspend fun editLastResult(lastResult: LastResult): ResultManage<Unit, DomainError> {
        return resultLocalDataSource.editLastResult(lastResult)
    }

    suspend fun deleteAllResults(): ResultManage<Unit, DomainError> {
        return resultLocalDataSource.deleteAllResults()
    }
}
