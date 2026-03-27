package com.example.courtgate.framework.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "code")
    val code: String,

    @ColumnInfo(name = "date")
    val date: Long,

    @ColumnInfo(name = "hour")
    val hour: String,

    @ColumnInfo(name = "user_id")
    val userId: String
)