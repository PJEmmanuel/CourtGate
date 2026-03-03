package com.example.courtgate.framework

import com.example.courtgate.data.datasources.LocalDataSource
import com.example.courtgate.domain.models.LastResult
import com.example.courtgate.framework.database.LastResultDAO
import com.example.courtgate.framework.database.LastResultEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class MatchRoomDataSource @Inject constructor(
    private val dao: LastResultDAO
) : LocalDataSource {

    override val getLastResult = dao.getLastResult().map { lastResultList ->
        lastResultList.map { it?.toDomain() }
    }

    override suspend fun insertLastResult(lastResult: LastResult) {
        return dao.insertLastResult(lastResult.toEntity())
    }

    override suspend fun deleteLastResult(lastResult: LastResult) {
        return dao.deleteLastResult(lastResult.toEntity())
    }

    override suspend fun editLastResult(lastResult: LastResult) {
        return dao.editLastResult(lastResult.toEntity())
    }


}

private fun LastResultEntity.toDomain(): LastResult {
    return LastResult(
        id = id,
        player1Set1 = this.player1Set1,
        player2Set1 = this.player2Set1,
        player1Set2 = this.player1Set2,
        player2Set2 = this.player2Set2,
        player1Set3 = this.player1Set3,
        player2Set3 = this.player2Set3
    )
}

private fun LastResult.toEntity(): LastResultEntity {
    return LastResultEntity(
        id = id,
        player1Set1 = this.player1Set1,
        player2Set1 = this.player2Set1,
        player1Set2 = this.player1Set2,
        player2Set2 = this.player2Set2,
        player1Set3 = this.player1Set3,
        player2Set3 = this.player2Set3
    )
}