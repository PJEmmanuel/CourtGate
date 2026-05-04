package com.example.courtgate.data

import com.example.courtgate.ResultManage
import com.example.courtgate.data.datasources.CourtLocalDataSource
import com.example.courtgate.data.datasources.CourtRemoteDataSource
import com.example.courtgate.data.datasources.SyncPreferencesDataSource
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.domain.models.FilterOption
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

class ManageCourtRepository @Inject constructor(
    private val remoteDataSource: CourtRemoteDataSource,
    private val localDataSource: CourtLocalDataSource,
    private val syncPreferencesDataSource: SyncPreferencesDataSource,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun syncStaticDataIfNeeded(): ResultManage<Unit, DomainError> {
        val today = LocalDate.now().toString()
        val roomEmpty = localDataSource.getCourtsCount() == 0
                && localDataSource.getScheduleCount() == 0

        val isNewDay = syncPreferencesDataSource.lastSyncDay.first() != today
        if (!roomEmpty && !isNewDay) return ResultManage.Success(Unit)

        // Sinc courts
        when (val courts = remoteDataSource.getAllCourt()) {
            is ResultManage.Success -> localDataSource.saveAllCourt(courts.data)
            is ResultManage.Failure -> {
                return ResultManage.Failure(courts.error)
            }
        }
        // Sinc schedules
        when (val schedule = remoteDataSource.getRegularHours()) {
            is ResultManage.Success -> localDataSource.saveRegularHours(schedule.data)
            is ResultManage.Failure -> {
                return ResultManage.Failure(schedule.error)
            }
        }
        syncPreferencesDataSource.saveLastSyncDay(today)
        return ResultManage.Success(Unit)
    }

    fun getAllCourtToShow(
        located: String?,
        selectedDay: Long,
        endSelectedDay: Long,
        currentDayStart: Instant,
        endSevenDaysFromNow: Instant
    ): Flow<List<Court>> = channelFlow {

        val syncResult = syncStaticDataIfNeeded()
        if (syncResult is ResultManage.Failure) {
            throw DomainException(syncResult.error)
        }

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
    }.flowOn(ioDispatcher)

    fun getFilterOption(): Flow<List<FilterOption>> =
        localDataSource.getDistinctLocatedTypes().distinctUntilChanged()
}