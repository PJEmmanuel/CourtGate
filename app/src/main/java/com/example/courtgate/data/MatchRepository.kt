package com.example.courtgate.data

import com.example.courtgate.data.datasources.LocalDataSource
import com.example.courtgate.domain.models.LastResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MatchRepository @Inject constructor(
    private val localDataSource: LocalDataSource
) {

    //TODO: Queda llamar a la base de datos remota del usuario.
    val lastResult: Flow<List<LastResult?>>
        get() = localDataSource.getLastResult

    //TODO: se usará más tarde en la pantalla Match
    suspend fun insertLastResult(lastResult: LastResult) {
        return localDataSource.insertLastResult(
            lastResult = lastResult
        )
    }
}