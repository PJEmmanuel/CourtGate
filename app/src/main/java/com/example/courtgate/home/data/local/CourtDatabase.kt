package com.example.courtgate.home.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.courtgate.home.data.local.entity.LastResultEntity

@Database(entities = [LastResultEntity::class], version = 1)
abstract class CourtDatabase : RoomDatabase() {
    abstract fun LastResultDAO(): LastResultDAO
}