package com.example.courtgate.data.datasources

import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.domain.models.FilterOption
import com.example.courtgate.domain.models.FreeHoursOfCourt
import com.example.courtgate.framework.database.CourtEntity
import com.example.courtgate.framework.database.CourtHourAvailability
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
    fun getDistinctLocatedTypes(): Flow<List<FilterOption>>
    suspend fun getScheduleCount(): Int
    suspend fun syncBookings(windowStart: Long, windowEnd: Long, bookings: List<CourtBooking>)
    fun getCourtByCode(code: String): Flow<Court>
    fun getHoursWithAvailability(
        code: String,
        dayStart: Long,
        dayEnd: Long
    ): Flow<List<FreeHoursOfCourt>>
}

