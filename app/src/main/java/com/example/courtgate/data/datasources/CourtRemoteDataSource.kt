package com.example.courtgate.data.datasources

import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.CourtBooking
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.ZonedDateTime

interface CourtRemoteDataSource {
    suspend fun getAllCourt(): List<Court>
    fun getBookingsSevenDaysAhead(currentDayStart: Instant, endSevenDaysFromNow: Instant): Flow<List<CourtBooking>>
    suspend fun getRegularHours(): List<String>
}