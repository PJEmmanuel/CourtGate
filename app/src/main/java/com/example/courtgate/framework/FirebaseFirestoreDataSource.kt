package com.example.courtgate.framework

import com.example.courtgate.ResultManage
import com.example.courtgate.data.datasources.CourtRemoteDataSource
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.NewCourtBooking
import com.example.courtgate.framework.remote.BookingDTO
import com.example.courtgate.framework.remote.CourtDTO
import com.example.courtgate.framework.remote.NewBookingDTO
import com.example.courtgate.framework.remote.ScheduleDTO
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.time.Instant
import java.util.Date
import javax.inject.Inject

const val COURTS_COLLECTION = "courts"
const val BOOKINGS_COLLECTION = "bookings"
const val FIELD_DATE = "date"
const val SETTINGS_COLLECTION = "settings"
const val SCHEDULES_DOCUMENT = "schedules"

class FirebaseFirestoreDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore,
) : CourtRemoteDataSource {

    // Obtengo todas las pistas. NUNCA cambia la oferta de pistas.
    override suspend fun getAllCourt(): ResultManage<List<Court>, DomainError> {
        return try {
            val courts = fireStore
                .collection(COURTS_COLLECTION)
                .get()
                .await().documents
                .mapNotNull { doc ->
                    val dto = doc.toObject(CourtDTO::class.java)?.toDomain()
                    dto?.copy(id = doc.id)
                }
            ResultManage.Success(courts)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            ResultManage.Failure(e.toRemoteError())
        }
    }

    // Obtengo los horarios ofertados.
    override suspend fun getRegularHours(): ResultManage<List<String>, DomainError> {
        return try {
            val document = fireStore
                .collection(SETTINGS_COLLECTION)
                .document(SCHEDULES_DOCUMENT)
                .get()
                .await()
            val scheduleDto = document.toObject(ScheduleDTO::class.java)
            ResultManage.Success(
                scheduleDto?.defaultHours ?: emptyList()
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            ResultManage.Failure(e.toRemoteError())
        }
    }

    override suspend fun setNewBooking(newBooking: NewCourtBooking): ResultManage<Unit, DomainError> {
        return try {
            val docId = buildBookingId(newBooking)
            val docRef = fireStore.collection(BOOKINGS_COLLECTION).document(docId)

            fireStore.runTransaction { tx ->
                val snap = tx.get(docRef)
                if (snap.exists()) throw SlotAlreadyTakenException()

                tx.set(docRef, newBooking.toDTO())
            }.await()

            ResultManage.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            ResultManage.Failure(e.toRemoteError())
        }
    }

    // Obtengo pistas a 7 días vista desde el día actual.
    override fun getBookingsSevenDaysAhead(
        currentDayStart: Instant,
        endSevenDaysFromNow: Instant
    ): Flow<List<CourtBooking>> {

        return callbackFlow {
            val query = fireStore.collection(BOOKINGS_COLLECTION)
                .whereGreaterThanOrEqualTo(FIELD_DATE, Timestamp(Date.from(currentDayStart)))
                .whereLessThan(FIELD_DATE, Timestamp(Date.from(endSevenDaysFromNow)))

            val subscription = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val bookings = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(BookingDTO::class.java)?.toDomain()?.copy(id = doc.id)
                }.orEmpty()
                trySend(bookings)
            }
            awaitClose { subscription.remove() }
        }
    }

    private fun Exception.toRemoteError(): DomainError =
        when (this) {
            is SlotAlreadyTakenException -> DomainError.Booking.SlotAlreadyTaken
            is FirebaseFirestoreException ->
                when (code) {
                    FirebaseFirestoreException.Code.PERMISSION_DENIED -> DomainError.Remote.AccessDenied
                    FirebaseFirestoreException.Code.NOT_FOUND -> DomainError.Remote.NotFound
                    FirebaseFirestoreException.Code.UNAVAILABLE,
                    FirebaseFirestoreException.Code.INTERNAL -> DomainError.Remote.ServerError

                    else -> DomainError.Remote.UnknownRemoteError
                }

            is IOException -> DomainError.Remote.ServerError // Sin red
            else -> DomainError.Remote.UnknownRemoteError
        }

    private fun buildBookingId(b: NewCourtBooking): String {
        val dayMillis = b.date.toEpochMilli()
        return "${b.code}_${dayMillis}_${b.hour}"
    }

    private class SlotAlreadyTakenException : RuntimeException()
}

fun NewCourtBooking.toDTO(): NewBookingDTO {
    return NewBookingDTO(
        code = this.code,
        date = Timestamp(this.date),
        hour = this.hour,
        userId = this.userId
    )
}

fun CourtDTO.toDomain(): Court {
    return Court(
        code = this.code.orEmpty(),
        name = this.name.orEmpty(),
        color = this.color.orEmpty(),
        located = this.located.orEmpty(),
        price = this.price ?: 11,
        image = this.image.orEmpty(),
        id = this.id.orEmpty()
    )
}

fun BookingDTO.toDomain(): CourtBooking? {
    return CourtBooking(
        id = this.id ?: "",
        code = this.code.orEmpty(),
        date = this.date?.toDate()?.toInstant() ?: return null,
        hour = this.hour.orEmpty(),
        userId = this.userId.orEmpty()
    )
}