package com.example.courtgate.data.datasources

import com.example.courtgate.ResultManage
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.NewCourtBooking
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface CourtRemoteDataSource {
    suspend fun getAllCourt(): ResultManage<List<Court>, DomainError>
    suspend fun getRegularHours(): ResultManage<List<String>, DomainError>
    suspend fun setNewBooking(newBooking: NewCourtBooking): ResultManage<Unit, DomainError>
    fun getBookingsSevenDaysAhead(
        currentDayStart: Instant,
        endSevenDaysFromNow: Instant
    ): Flow<List<CourtBooking>>
}
