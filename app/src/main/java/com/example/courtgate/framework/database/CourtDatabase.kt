package com.example.courtgate.framework.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LastResultEntity::class], version = 1)
abstract class CourtDatabase : RoomDatabase() {
    abstract fun LastResultDAO(): LastResultDAO
}