package com.example.courtgate

import com.example.courtgate.data.ManageCourtRepository
import com.example.courtgate.data.datasources.CourtLocalDataSource
import com.example.courtgate.data.datasources.CourtRemoteDataSource
import com.example.courtgate.data.datasources.SyncPreferencesDataSource
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.FilterOption
import com.example.courtgate.domain.models.FreeHoursOfCourt
import com.example.courtgate.domain.models.NewCourtBooking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.TestDispatcher
import java.time.Instant

fun buildManageCourtRepository(
    localDS: List<Court> = emptyList(),
    schedule: List<String> = emptyList(),
    bookings: List<CourtBooking> = emptyList(),
    remoteDS: List<Court> = emptyList(),
    remoteError: DomainError? = null,
    syncPreferenceDS: String = "",
    ioDispatcher: TestDispatcher
): ManageCourtRepository {

    return ManageCourtRepository(
        remoteDataSource = FakeRemoteDS(remoteDS, bookings, schedule, remoteError),
        localDataSource = FakeLocalDS(localDS, schedule, bookings),
        syncPreferencesDataSource = FakeSyncPreferenceDS(syncPreferenceDS),
        ioDispatcher = ioDispatcher
    )
}

class FakeSyncPreferenceDS(syncPreferenceDS: String) : SyncPreferencesDataSource {
    private val _lastSyncDay = MutableStateFlow<String?>(syncPreferenceDS.takeIf { it.isNotEmpty() })

    override val lastSyncDay: Flow<String?> = _lastSyncDay

    override suspend fun saveLastSyncDay(day: String) {
        _lastSyncDay.value = day
    }
}

class FakeRemoteDS(
    remoteDS: List<Court>,
    bookings: List<CourtBooking>,
    schedule: List<String>,
    remoteError: DomainError?
) :
    CourtRemoteDataSource {

    var errorInMemory = remoteError
    var courtInMemory = remoteDS
    val bookingInMemory = MutableStateFlow(bookings)
    var scheduleInMemory = schedule

    override suspend fun getAllCourt(): ResultManage<List<Court>, DomainError> {
        return errorInMemory?.let { ResultManage.Failure(it) }
            ?: ResultManage.Success(courtInMemory)

    }

    override suspend fun getRegularHours(): ResultManage<List<String>, DomainError> {
        return errorInMemory?.let { ResultManage.Failure(it) }
            ?: ResultManage.Success(scheduleInMemory)
    }

    override suspend fun setNewBooking(newBooking: NewCourtBooking): ResultManage<Unit, DomainError> {
        TODO("Not yet implemented")
    }

    override fun getBookingsSevenDaysAhead(
        currentDayStart: Instant,
        endSevenDaysFromNow: Instant
    ): Flow<List<CourtBooking>> {
        return bookingInMemory
    }

}

class FakeLocalDS(
    localDS: List<Court>, schedule: List<String>, bookings: List<CourtBooking>
) : CourtLocalDataSource {
    val courtInMemory = MutableStateFlow(localDS)

    var bookingInMemory = bookings
    var scheduleInMemory = schedule

    override suspend fun saveAllCourt(courts: List<Court>) {
        courtInMemory.value = courts
    }

    override suspend fun saveRegularHours(schedule: List<String>) {
        scheduleInMemory = schedule
    }

    override suspend fun saveAllBookings(bookings: List<CourtBooking>) {
        bookingInMemory = bookings
    }

    override fun getAvailableCourts(
        locatedFilter: String?,
        selectedDay: Long,
        endSelectedDay: Long
    ): Flow<List<Court>> {
        return courtInMemory.map { listCourt ->
            if (locatedFilter == null) listCourt else listCourt.filter { it.located == locatedFilter }
        }
    }

    override suspend fun getCourtsCount(): Int {
        return courtInMemory.value.size
    }

    override fun getDistinctLocatedTypes(): Flow<List<FilterOption>> {
        return courtInMemory.map { listCourt ->
            listCourt.distinctBy { court ->
                court.located
            }.map {
                FilterOption(
                    located = it.located,
                    isSelected = false
                )
            }
        }
    }

    override suspend fun getScheduleCount(): Int {
        return scheduleInMemory.size
    }

    override suspend fun syncBookings(
        windowStart: Long,
        windowEnd: Long,
        bookings: List<CourtBooking>
    ) {
        bookingInMemory = bookings
    }

    override fun getCourtByCode(code: String): Flow<Court> {
        TODO("Not yet implemented")
    }

    override fun getHoursWithAvailability(
        code: String,
        dayStart: Long,
        dayEnd: Long
    ): Flow<List<FreeHoursOfCourt>> {
        TODO("Not yet implemented")
    }
}
