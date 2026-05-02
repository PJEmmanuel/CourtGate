package com.example.courtgate.domain.models

import java.time.Instant

data class NewCourtBooking(
    val code: String,
    val date: Instant,
    val hour: String,
    val userId: String
)
