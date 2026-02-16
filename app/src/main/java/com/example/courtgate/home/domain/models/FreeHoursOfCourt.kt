package com.example.courtgate.home.domain.models

data class FreeHoursOfCourt(
    val code: String,
    val hour: String,
    val isFree: Boolean,
    val isSelected: Boolean
)
