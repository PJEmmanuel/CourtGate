package com.example.courtgate.framework

import com.example.courtgate.domain.models.NewCourtBooking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class BookingIdContractTest {

    @Test
    fun `buildBookingId matches the format enforced by Firestore rules`() {
        val date = Instant.parse("2025-01-10T00:00:00Z")
        val booking = NewCourtBooking(
            code = "pista_central",
            date = date,
            hour = "10:00",
            userId = "userTest",
            startsAt = Instant.parse("2025-01-10T10:00:00Z")
        )

        val result = buildBookingId(booking)

        assertEquals("pista_central_${date.toEpochMilli()}_10:00", result)
    }
}