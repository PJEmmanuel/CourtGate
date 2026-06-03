package com.example.courtgate.framework.remote

import com.google.firebase.Timestamp

data class NewBookingDTO(
    val code: String? = null,
    val date: Timestamp? = null,
    val hour: String? = null,
    val userId: String? = null,
    val startsAt: Timestamp? = null,
)