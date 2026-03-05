package com.example.courtgate.core

import com.example.courtgate.domain.models.LastResult

fun lastResultTest(
    id: Int = 0,
    player1Set1: Int = 0,
    player2Set1: Int = 0,
    player1Set2: Int = 0,
    player2Set2: Int = 0,
    player1Set3: Int = 0,
    player2Set3: Int = 0
) = LastResult(
    id = id,
    player1Set1 = player1Set1,
    player2Set1 = player2Set1,
    player1Set2 = player1Set2,
    player2Set2 = player2Set2,
    player1Set3 = player1Set3,
    player2Set3 = player2Set3
)