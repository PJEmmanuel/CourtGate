package com.example.courtgate.data

import com.example.courtgate.data.datasources.ResultLocalDataSource
import com.example.courtgate.domain.models.LastResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MatchRepository @Inject constructor(
    private val resultLocalDataSource: ResultLocalDataSource
) {

    //TODO: Queda llamar a la base de datos remota del usuario.
    val lastResult: Flow<List<LastResult>>
        get() = resultLocalDataSource.getLastResult.map { it.filterNotNull() }

    //TODO: se usará más tarde en la pantalla Match
    suspend fun insertLastResult(lastResult: LastResult) {
        return resultLocalDataSource.insertLastResult(
            lastResult = lastResult
        )
    }

    /*Step 5 — Update MatchRepository
The repository becomes a thin pass-through for writes, since there's no SSOT concern here (no remote source yet):

suspend fun insertLastResult(lastResult: LastResult): ResultCourt<Unit> {
    return resultLocalDataSource.insertLastResult(lastResult)
}
// same for delete and edit

The lastResult Flow property stays unchanged.*/

    //TODO: se usará más tarde en la pantalla Match
    suspend fun deleteLastResult(lastResult: LastResult) {
        return resultLocalDataSource.deleteLastResult(lastResult)
    }

    //TODO: se usará más tarde en la pantalla Match
    suspend fun editLastResult(lastResult: LastResult) {
        return resultLocalDataSource.editLastResult(lastResult)
    }
}