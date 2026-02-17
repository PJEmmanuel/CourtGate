package com.example.courtgate.framework.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LastResultDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
     fun insertLastResult(lastResultEntity: LastResultEntity)

    @Query("SELECT * FROM LAST_RESULT")
     fun getLastResult(): List<LastResultEntity>

}