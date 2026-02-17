package com.example.courtgate.framework.remote

import com.google.firebase.Timestamp

data class CourtBookingDTO(
    val code: String? = null,
    val date: Timestamp? = null,
    val hour: String? = null,
    val userId: String? = null
)
