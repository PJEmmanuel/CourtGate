package com.example.courtgate.home.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.courtgate.home.data.local.entity.LastResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LastResultDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
     fun insertLastResult(lastResultEntity: LastResultEntity)

    @Query("SELECT * FROM LAST_RESULT")
     fun getLastResult(): List<LastResultEntity>

}