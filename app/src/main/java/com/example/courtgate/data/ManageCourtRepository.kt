package com.example.courtgate.data

import com.example.courtgate.ResultManage
import com.example.courtgate.data.datasources.CourtLocalDataSource
import com.example.courtgate.data.datasources.CourtRemoteDataSource
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.domain.models.FilterOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

class ManageCourtRepository @Inject constructor(
    private val remoteDataSource: CourtRemoteDataSource,
    private val localDataSource: CourtLocalDataSource,
) {
    fun getAllCourtToShow(
        located: String?,
        selectedDay: Long,
        endSelectedDay: Long,
        currentDayStart: Instant,
        endSevenDaysFromNow: Instant
    ): Flow<List<Court>> = channelFlow {

        ensureStaticDataLoaded() //TODO: Actualiza en cada llamada

        launch {
            remoteDataSource.getBookingsSevenDaysAhead(currentDayStart, endSevenDaysFromNow)
                .collect { bookings ->
                    localDataSource.syncBookings(
                        currentDayStart.toEpochMilli(),
                        endSevenDaysFromNow.toEpochMilli(),
                        bookings
                    )
                }
        }

        localDataSource.getAvailableCourts(located, selectedDay, endSelectedDay)
            .distinctUntilChanged()
            .collect { courts -> send(courts) }
    }.flowOn(Dispatchers.IO)

    suspend fun getFilterOption(): ResultManage<List<FilterOption>, DomainError> {
        return try {
            ResultManage.Success(localDataSource.getDistinctLocatedTypes())
        } catch (e: Exception) {
            ResultManage.Failure(DomainError.Local.UnknownLocalError)
        }
    }

    private suspend fun ensureStaticDataLoaded() {
        when (val courts = remoteDataSource.getAllCourt()) {
            is ResultManage.Success -> localDataSource.saveAllCourt(courts.data)
            is ResultManage.Failure -> throw DomainException(courts.error)
        }
        when (val schedule = remoteDataSource.getRegularHours()) {
            is ResultManage.Success -> localDataSource.saveRegularHours(schedule.data)
            is ResultManage.Failure -> throw DomainException(schedule.error)
        }
    }
}