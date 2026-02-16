package com.example.courtgate.home.domain.repository

import com.example.courtgate.home.domain.models.CourtBooking
import com.example.courtgate.home.domain.models.CourtList
import com.example.courtgate.home.domain.models.LastResult
import java.time.Instant

interface HomeRepository {

    fun insertLastResult(lastResult: LastResult)
    fun getLastResult(): List<LastResult>

    suspend fun getAllCourtToShow(): Result<List<CourtList>>
    suspend fun getBookingsByDate(
        startOfDay: Instant,
        endOfDay: Instant
    ): Result<List<CourtBooking>>

    suspend fun getCourtSelectedByCode(code: String): Result<CourtList>
    suspend fun getFreeHoursOnReservedCourts(code: String, startOfDay: Instant, endOfDay: Instant): Result<List<CourtBooking>>
}