package com.example.courtgate.home.domain.usecase

data class BookingUseCases(
    val getCourtSelectedByCode: GetCourtSelectedByCode,
    val getFreeHoursOnReservedCourts: GetFreeHoursOnReservedCourts
)
