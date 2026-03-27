package com.example.courtgate.domain.models

import java.time.ZonedDateTime

data class CourtBooking(
    val id: String,
    val code: String,
    val date: ZonedDateTime,
    val hour: String,
    val userId: String
)