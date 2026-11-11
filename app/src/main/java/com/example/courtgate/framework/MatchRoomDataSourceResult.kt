package com.example.courtgate.framework

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabaseCorruptException
import android.database.sqlite.SQLiteFullException
import com.example.courtgate.ResultManage
import com.example.courtgate.data.datasources.ResultLocalDataSource
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.domain.models.LastResult
import com.example.courtgate.framework.database.LastResultDAO
import com.example.courtgate.framework.database.LastResultEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MatchRoomDataSourceResult @Inject constructor(
    private val dao: LastResultDAO
) : ResultLocalDataSource {

    override val getLastResult = dao.getLastResult()
        .map { lastResultList -> lastResultList.map { it?.toDomain() } }
        .catch { e -> throw DomainException(e.toLocalError()) }

    override suspend fun insertLastResult(lastResult: LastResult) =
        runLocal { dao.insertLastResult(lastResult.toEntity()) }

    override suspend fun deleteLastResult(lastResult: LastResult) =
        runLocal { dao.deleteLastResult(lastResult.toEntity()) }

    override suspend fun editLastResult(lastResult: LastResult) =
        runLocal { dao.editLastResult(lastResult.toEntity()) }

    override suspend fun deleteAllResults() =
        runLocal { dao.deleteAllResults() }

    private suspend fun runLocal(block: suspend () -> Unit): ResultManage<Unit, DomainError> {
        return try {
            block()
            ResultManage.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            ResultManage.Failure(e.toLocalError())
        }
    }

    private fun Throwable.toLocalError(): DomainError =
        when (this) {
            is DomainException -> error
            is SQLiteFullException -> DomainError.Local.DiskFull
            is SQLiteConstraintException -> DomainError.Local.ConstraintViolation
            is SQLiteDatabaseCorruptException -> DomainError.Local.DatabaseCorrupted
            else -> DomainError.Local.UnknownLocalError
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
