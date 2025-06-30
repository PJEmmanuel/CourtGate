package com.example.courtgate.home.data.mapper

import com.example.courtgate.home.data.local.entity.LastResultEntity
import com.example.courtgate.home.domain.models.LastResult

fun LastResultEntity.toDomain() : LastResult{
    return LastResult(
        id = id,
        player1Set1 = this.player1Set1,
        player2Set1 = this.player2Set1,
        player1Set2 = this.player1Set1,
        player2Set2 = this.player2Set2,
        player1Set3 = this.player1Set3,
        player2Set3 = this.player2Set3
    )
}

fun LastResult.toEntity() : LastResultEntity{
    return LastResultEntity(
        id = id,
        player1Set1 = this.player1Set1,
        player2Set1 = this.player2Set1,
        player1Set2 = this.player1Set1,
        player2Set2 = this.player2Set2,
        player1Set3 = this.player1Set3,
        player2Set3 = this.player2Set3
    )
}