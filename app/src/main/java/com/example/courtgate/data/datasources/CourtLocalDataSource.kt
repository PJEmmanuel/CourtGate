package com.example.courtgate.data.datasources

import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.domain.models.FilterOption
import kotlinx.coroutines.flow.Flow

interface CourtLocalDataSource {
    suspend fun saveAllCourt(courts: List<Court>)
    suspend fun saveRegularHours(schedule: List<String>)
    suspend fun saveAllBookings(bookings: List<CourtBooking>)
    fun getAvailableCourts(
        locatedFilter: String?,
        selectedDay: Long,
        endSelectedDay: Long
    ): Flow<List<Court>>

    suspend fun getCourtsCount(): Int
    suspend fun getDistinctLocatedTypes(): List<FilterOption>
    suspend fun getScheduleCount(): Int
    suspend fun syncBookings(windowStart: Long, windowEnd: Long, bookings: List<CourtBooking>)
}

