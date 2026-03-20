package com.example.courtgate.framework.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "last_result")
data class LastResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "set_1_player_1")
    val player1Set1: Int,

    @ColumnInfo(name = "set_1_player_2")
    val player2Set1: Int,

    @ColumnInfo(name = "set_2_player_1")
    val player1Set2: Int,

    @ColumnInfo(name = "set_2_player_2")
    val player2Set2: Int,

    @ColumnInfo(name = "set_3_player_1")
    val player1Set3: Int,

    @ColumnInfo(name = "set_3_player_2")
    val player2Set3: Int
)