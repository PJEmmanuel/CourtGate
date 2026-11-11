package com.example.courtgate.fakes

import com.example.courtgate.ResultManage
import com.example.courtgate.data.datasources.CourtRemoteDataSource
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.NewCourtBooking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

// Firestore en memoria: una pista, dos horas ofertadas y las reservas en un StateFlow,
// así los listeners (Find, Booking, Mis reservas) ven los cambios como con Firestore.
@Singleton
class FakeCourtRemoteDataSource @Inject constructor() : CourtRemoteDataSource {

    val bookings = MutableStateFlow<List<CourtBooking>>(emptyList())

    // Lo que la app ha pedido escribir, para comprobarlo en los tests
    val createdBookings = mutableListOf<NewCourtBooking>()
    val deletedBookingIds = mutableListOf<String>()

    override suspend fun getAllCourt(): ResultManage<List<Court>, DomainError> =
        ResultManage.Success(listOf(TEST_COURT))

    override suspend fun getRegularHours(): ResultManage<List<String>, DomainError> =
        ResultManage.Success(TEST_HOURS)

    override suspend fun setNewBooking(newBooking: NewCourtBooking): ResultManage<Unit, DomainError> {
        createdBookings += newBooking
        bookings.update {
            it + CourtBooking(
                id = "${newBooking.code}_${newBooking.date.toEpochMilli()}_${newBooking.hour}",
                code = newBooking.code,
                date = newBooking.date,
                hour = newBooking.hour,
                userId = newBooking.userId
            )
        }
        return ResultManage.Success(Unit)
    }

    override fun getBookingsSevenDaysAhead(
        currentDayStart: Instant,
        endSevenDaysFromNow: Instant
    ): Flow<List<CourtBooking>> = bookings

    override fun getMyBookings(currentUser: String, startAt: Instant): Flow<List<CourtBooking>> =
        bookings.map { list -> list.filter { it.userId == currentUser } }

    override suspend fun deleteMyBookings(docId: String): ResultManage<Unit, DomainError> {
        deletedBookingIds += docId
        bookings.update { list -> list.filterNot { it.id == docId } }
        return ResultManage.Success(Unit)
    }

    override suspend fun deleteAllUserBookings(userId: String): ResultManage<Unit, DomainError> {
        bookings.update { list -> list.filterNot { it.userId == userId } }
        return ResultManage.Success(Unit)
    }

    companion object {
        val TEST_COURT = Court(
            id = "court-1",
            code = "pista_central",
            name = "Pista Central",
            color = "Azul",
            located = "indoor",
            price = 12,
            image = ""
        )
        val TEST_HOURS = listOf("10:00", "18:00")
    }
}
