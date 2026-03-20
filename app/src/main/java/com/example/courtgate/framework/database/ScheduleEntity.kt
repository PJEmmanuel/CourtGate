package com.example.courtgate.framework.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey
    val id: String = "schedules", //TODO: Mejorar hardcode. Coger nombre directamente de firestore?
    @ColumnInfo(name = "default_hours")
    val defaultHours: String
)