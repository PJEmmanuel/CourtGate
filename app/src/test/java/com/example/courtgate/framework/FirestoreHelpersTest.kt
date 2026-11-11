package com.example.courtgate.framework

import app.cash.turbine.test
import com.example.courtgate.ResultManage
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.framework.remote.BookingDTO
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.IOException
import java.time.Instant
import java.util.Date

class FirestoreHelpersTest {

    // FirebaseFirestoreException.Code no se puede cargar en la JVM (su inicializador usa SparseArray
    // de Android): el mapeo de códigos de Firestore no se prueba aquí.

    // --- firestoreCall ---

    @Test
    fun `firestoreCall wraps the result in Success`() = runTest {
        assertEquals(ResultManage.Success(42), firestoreCall { 42 })
    }

    @Test
    fun `firestoreCall maps slot taken, no network and unknown errors`() = runTest {
        assertEquals(
            ResultManage.Failure(DomainError.Booking.SlotAlreadyTaken),
            firestoreCall<Unit> { throw SlotAlreadyTakenException() }
        )
        assertEquals(
            ResultManage.Failure(DomainError.Remote.ServerError),
            firestoreCall<Unit> { throw IOException() }
        )
        assertEquals(
            ResultManage.Failure(DomainError.Remote.UnknownRemoteError),
            firestoreCall<Unit> { throw IllegalStateException() }
        )
    }

    @Test
    fun `firestoreCall rethrows cancellation`() = runTest {
        try {
            firestoreCall<Unit> { throw CancellationException() }
            fail("CancellationException should be rethrown")
        } catch (_: CancellationException) {
        }
    }

    // --- observeBookings ---

    private val registration: ListenerRegistration = mock()
    private val query: Query = mock()
    private val listener = argumentCaptor<EventListener<QuerySnapshot>>()

    private fun stubListener() {
        whenever(query.addSnapshotListener(any<EventListener<QuerySnapshot>>())) doReturn registration
    }

    private fun bookingDoc(id: String, dto: BookingDTO?): DocumentSnapshot = mock {
        on { this.id } doReturn id
        on { toObject(BookingDTO::class.java) } doReturn dto
    }

    @Test
    fun `observeBookings emits the mapped bookings and skips documents without date`() = runTest {
        stubListener()
        val date = Date.from(Instant.parse("2026-11-12T00:00:00Z"))
        val valid = bookingDoc(
            "doc1",
            BookingDTO(code = "pista_1", date = Timestamp(date), hour = "10:00", userId = "u1")
        )
        val withoutDate = bookingDoc("doc2", BookingDTO(code = "pista_2"))
        val snapshot: QuerySnapshot = mock { on { documents } doReturn listOf(valid, withoutDate) }

        query.observeBookings().test {
            verify(query).addSnapshotListener(listener.capture())
            listener.firstValue.onEvent(snapshot, null)

            assertEquals(
                listOf(CourtBooking("doc1", "pista_1", date.toInstant(), "10:00", "u1")),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
        verify(registration).remove()
    }
}
