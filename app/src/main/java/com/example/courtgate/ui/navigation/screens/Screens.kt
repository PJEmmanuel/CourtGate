package com.example.courtgate.ui.navigation.screens

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object SignUp

@Serializable
object Home

@Serializable
object FindCourt

@Serializable
data class Booking(
    val code: String,
    val date: String
)
