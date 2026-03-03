package com.example.courtgate.framework.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LastResultDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLastResult(lastResultEntity: LastResultEntity)

    @Query("SELECT * FROM LAST_RESULT")
    fun getLastResult(): Flow<List<LastResultEntity?>>

}