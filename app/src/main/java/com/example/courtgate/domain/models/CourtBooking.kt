package com.example.courtgate.domain.models

import java.time.Instant

data class CourtBooking(
    val id: String,
    val code: String,
    val date: Instant,
    val hour: String,
    val userId: String
)