package com.example.courtgate.usecases.booking

data class BookingUseCases(
    val getCourtSelectedByCode: GetCourtSelectedByCode,
    val getFreeHoursOnReservedCourts: GetFreeHoursOnReservedCourts
)