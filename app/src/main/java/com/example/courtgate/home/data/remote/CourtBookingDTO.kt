package com.example.courtgate.home.data.remote

import com.google.firebase.Timestamp

data class CourtBookingDTO(
    val code: String? = null,
    val date: Timestamp? = null,
    val hour: String? = null,
    val userId: String? = null
)
