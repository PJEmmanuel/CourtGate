package com.example.courtgate.home.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.courtgate.home.data.local.entity.LastResultEntity

@Dao
interface LastResultDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLastResult(lastResultEntity: LastResultEntity)

    @Query("SELECT * FROM LAST_RESULT")
    suspend fun getLastResult(): List<LastResultEntity>

}