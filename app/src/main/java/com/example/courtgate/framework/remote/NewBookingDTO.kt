package com.example.courtgate.framework.remote

import com.google.firebase.Timestamp

data class NewBookingDTO(
    val code: String,
    val date: Timestamp,
    val hour: String,
    val userId: String,
    val startsAt: Timestamp,
)
