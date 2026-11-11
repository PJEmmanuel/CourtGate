package com.example.courtgate.data.datasources

import com.example.courtgate.ResultManage
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.LastResult
import kotlinx.coroutines.flow.Flow

interface ResultLocalDataSource {

    val getLastResult: Flow<List<LastResult?>>
    suspend fun insertLastResult(lastResult: LastResult): ResultManage<Unit, DomainError>
    suspend fun deleteLastResult(lastResult: LastResult): ResultManage<Unit, DomainError>
    suspend fun editLastResult(lastResult: LastResult): ResultManage<Unit, DomainError>
    suspend fun deleteAllResults(): ResultManage<Unit, DomainError>
}
