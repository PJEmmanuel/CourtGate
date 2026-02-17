package com.example.courtgate.domain.models

data class CourtList(
    val code : String,
    val name: String,
    val color: String,
    val located: String,
    val price: Int,
    val image: String
)