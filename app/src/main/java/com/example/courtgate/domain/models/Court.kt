package com.example.courtgate.domain.models

data class Court(
    val id : String,
    val code : String,
    val name: String,
    val color: String,
    val located: String,
    val price: Int,
    val image: String
)