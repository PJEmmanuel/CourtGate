package com.example.courtgate.home.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "last_result")
data class LastResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    @ColumnInfo("first_set")
    val player1Set1: Int,
    val player2Set1: Int,
    @ColumnInfo("second_set")
    val player1Set2: Int,
    val player2Set2: Int,
    @ColumnInfo("third_set")
    val player1Set3: Int,
    val player2Set3: Int
)
