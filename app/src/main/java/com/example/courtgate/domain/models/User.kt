package com.example.courtgate.domain.models

import java.time.Instant

data class User(
    val uid: String,
    val email: String,
    val createdAt: Instant? = null
)
