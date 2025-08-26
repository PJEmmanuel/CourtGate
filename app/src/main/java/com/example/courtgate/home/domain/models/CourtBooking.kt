package com.example.courtgate.home.domain.models

import java.time.ZonedDateTime

data class CourtBooking (
    val courtId: String,
    val date: ZonedDateTime,
    val hour: String,
    val userId: String
)