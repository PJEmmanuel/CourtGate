package com.example.courtgate.framework.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LastResultDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLastResult(lastResultEntity: LastResultEntity)

    // El id es autoincremental: el más alto es el último resultado guardado
    @Query("SELECT * FROM LAST_RESULT ORDER BY id DESC")
    fun getLastResult(): Flow<List<LastResultEntity>>

    @Delete()
    suspend fun deleteLastResult(lastResultEntity: LastResultEntity)

    @Update
    suspend fun  editLastResult(lastResultEntity: LastResultEntity)

    @Query("DELETE FROM LAST_RESULT")
    suspend fun deleteAllResults()

}
