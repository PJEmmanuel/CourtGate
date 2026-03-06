package com.example.courtgate.data

import com.example.courtgate.ResultCourt
import com.example.courtgate.data.datasources.CourtRemoteDataSource
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.domain.models.CourtList
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val courtRemoteDataSource: CourtRemoteDataSource
) {

    suspend fun getAllCourtToShow(): ResultCourt<List<CourtList>> {
        return courtRemoteDataSource.getAllCourtToShow()
    }

    suspend fun getCourtSelectedByCode(code: String): ResultCourt<CourtList> {
        return courtRemoteDataSource.getCourtSelectedByCode(code)
    }

    fun getBookingsByDate(
        startOfDay: Instant,
        endOfDay: Instant
    ): Flow<ResultCourt<List<CourtBooking>>> {
        return courtRemoteDataSource.getBookingsByDate(startOfDay, endOfDay)
    }

    fun getFreeHoursOnReservedCourts(
        code: String,
        startOfDay: Instant,
        endOfDay: Instant
    ): Flow<ResultCourt<List<CourtBooking>>> {
        return courtRemoteDataSource.getFreeHoursOnReservedCourts(code, startOfDay, endOfDay)
    }
}