package com.example.courtgate.framework

import com.example.courtgate.data.datasources.CourtLocalDataSource
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.domain.models.FilterOption
import com.example.courtgate.domain.models.FreeHoursOfCourt
import com.example.courtgate.framework.database.BookingEntity
import com.example.courtgate.framework.database.CourtEntity
import com.example.courtgate.framework.database.CourtHourAvailability
import com.example.courtgate.framework.database.ManageCourtDAO
import com.example.courtgate.framework.database.ScheduleEntity
import hilt_aggregated_deps._dagger_hilt_android_internal_managers_HiltWrapper_SavedStateHandleModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ManageCourtRoomDataSource @Inject constructor(
    private val manageCourtDAO: ManageCourtDAO
) : CourtLocalDataSource {

    override suspend fun saveAllCourt(courts: List<Court>) {
        val courtEntity = courts.map { it.toCourEntity() }
        manageCourtDAO.insertAllCourt(courtEntity)
    }

    override suspend fun saveRegularHours(schedule: List<String>) {
        val scheduleEntity = schedule.map { it.toScheduleEntity() }
        manageCourtDAO.insertRegularHours(scheduleEntity)
    }

    override suspend fun saveAllBookings(bookings: List<CourtBooking>) {
        val entities = bookings.map { it.toBookingEntity() }
        manageCourtDAO.insertAllBookings(entities)
    }

    override suspend fun getCourtsCount() = manageCourtDAO.getCourtCount()

    override suspend fun getScheduleCount() = manageCourtDAO.getScheduleCount()

    override suspend fun syncBookings(
        windowStart: Long,
        windowEnd: Long,
        bookings: List<CourtBooking>
    ) {
        val bookingsEntity = bookings.map { it.toBookingEntity() }
        manageCourtDAO.syncBookingsForDay(
            windowStart = windowStart,
            windowEnd = windowEnd,
            bookings = bookingsEntity
        )
    }

    override fun getCourtByCode(code: String): Flow<Court> {
        return manageCourtDAO.getCourtByCode(code).map { it.toCourtDomain() }
    }

    override fun getHoursWithAvailability(
        code: String,
        dayStart: Long,
        dayEnd: Long
    ): Flow<List<FreeHoursOfCourt>> {
        return manageCourtDAO.getHoursWithAvailability(code, dayStart, dayEnd).map { list ->
            list.map {
                it.toFreeHoursOfCourt()
            }
        }
    }

    override fun getAvailableCourts(
        locatedFilter: String?,
        selectedDay: Long,
        endSelectedDay: Long
    ): Flow<List<Court>> {
        val courts = manageCourtDAO.getAvailableCourts(
            locatedFilter = locatedFilter,
            selectedDay = selectedDay,
            endSelectedDay = endSelectedDay
        )
        return courts.map { listCourtEntity ->
            listCourtEntity.map { it.toCourtDomain() }
        }
    }

    override fun getDistinctLocatedTypes(): Flow<List<FilterOption>> {
        val filter = manageCourtDAO.getDistinctLocatedTypes()
        return filter.map { listFilter ->
            listFilter.map {
                it.toFilterOption()
            }
        }
    }
}

private fun CourtHourAvailability.toFreeHoursOfCourt(): FreeHoursOfCourt {
    return FreeHoursOfCourt(
        hour = this.hour,
        isFree = this.isFree,
    )
}

private fun String.toFilterOption(): FilterOption {
    return FilterOption(
        located = this,
        isSelected = false
    )
}

private fun Court.toCourEntity(): CourtEntity {
    return CourtEntity(
        id = this.id,
        code = this.code,
        name = this.name,
        color = this.color,
        image = this.image,
        located = this.located,
        price = this.price
    )
}

private fun CourtEntity.toCourtDomain(): Court {
    return Court(
        id = this.id,
        code = this.code,
        name = this.name,
        color = this.color,
        image = this.image,
        located = this.located,
        price = this.price
    )
}

private fun String.toScheduleEntity() = ScheduleEntity(this)

private fun CourtBooking.toBookingEntity(): BookingEntity {
    return BookingEntity(
        id = this.id,
        code = this.code,
        date = this.date.toEpochMilli(),
        hour = this.hour,
        userId = this.userId
    )
}