package com.example.courtgate.home.domain.models

import androidx.room.ColumnInfo

data class LastResult(
    val id : Int = 0, // ?????????
    val player1Set1: Int,
    val player2Set1: Int,
    val player1Set2: Int,
    val player2Set2: Int,
    val player1Set3: Int,
    val player2Set3: Int
)
