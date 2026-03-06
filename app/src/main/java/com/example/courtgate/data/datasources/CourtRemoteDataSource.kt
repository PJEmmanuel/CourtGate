package com.example.courtgate.data.datasources

import com.example.courtgate.ResultCourt
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.domain.models.CourtList
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface CourtRemoteDataSource {
    suspend fun getAllCourtToShow(): ResultCourt<List<CourtList>>
    suspend fun getCourtSelectedByCode(code: String): ResultCourt<CourtList>
    fun getBookingsByDate(
        startOfDay: Instant,
        endOfDay: Instant
    ): Flow<ResultCourt<List<CourtBooking>>>

    fun getFreeHoursOnReservedCourts(
        code: String,
        startOfDay: Instant,
        endOfDay: Instant
    ): Flow<ResultCourt<List<CourtBooking>>>
}