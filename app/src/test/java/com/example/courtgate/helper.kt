package com.example.courtgate

import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.domain.models.FilterOption
import java.time.Instant

fun createCourtBookingTest(id: Int) = CourtBooking(
    id = id.toString(),
    code = "pista_central",
    date = Instant.parse("2025-10-12T00:00:00Z"),
    hour = "12:30",
    userId = "user_test_$id"
)

fun createCourtTest(id: Int, located: String) = Court(
    id = "$id",
    code = "pista_$id",
    name = "Pista $id",
    color = "Azul",
    located = located,
    price = 15,
    image = "https://example.com/court.jpg"
)

fun createFilterOptionsTest(located: String, isSelected: Boolean) = FilterOption(
    located = located,
    isSelected = isSelected
)