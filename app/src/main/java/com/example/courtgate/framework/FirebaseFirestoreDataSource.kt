package com.example.courtgate.framework

import com.example.courtgate.ResultManage
import com.example.courtgate.data.datasources.CourtRemoteDataSource
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.domain.models.DomainException
import com.example.courtgate.domain.models.NewCourtBooking
import com.example.courtgate.framework.remote.BookingDTO
import com.example.courtgate.framework.remote.CourtDTO
import com.example.courtgate.framework.remote.NewBookingDTO
import com.example.courtgate.framework.remote.ScheduleDTO
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
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
const val FIELD_START_AT = "startsAt"
const val FIELD_USER_ID = "userId"
private const val MAX_WRITES_PER_TRANSACTION = 500

internal fun buildBookingId(b: NewCourtBooking): String {
    val dayMillis = b.date.toEpochMilli()
    return "${b.code}_${dayMillis}_${b.hour}"
}

class FirebaseFirestoreDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore,
) : CourtRemoteDataSource {

    // Obtengo todas las pistas. NUNCA cambia la oferta de pistas.
    override suspend fun getAllCourt(): ResultManage<List<Court>, DomainError> = firestoreCall {
        fireStore
            .collection(COURTS_COLLECTION)
            .get()
            .await().documents
            .mapNotNull { doc ->
                val dto = doc.toObject(CourtDTO::class.java)?.toDomain()
                dto?.copy(id = doc.id)
            }
    }

    // Obtengo los horarios ofertados.
    override suspend fun getRegularHours(): ResultManage<List<String>, DomainError> = firestoreCall {
        val document = fireStore
            .collection(SETTINGS_COLLECTION)
            .document(SCHEDULES_DOCUMENT)
            .get()
            .await()
        val scheduleDto = document.toObject(ScheduleDTO::class.java)
        scheduleDto?.defaultHours ?: emptyList()
    }

    override suspend fun setNewBooking(newBooking: NewCourtBooking): ResultManage<Unit, DomainError> =
        firestoreCall {
            val docId = buildBookingId(newBooking)
            val docRef = fireStore.collection(BOOKINGS_COLLECTION).document(docId)

            fireStore.runTransaction { tx ->
                val snap = tx.get(docRef)
                if (snap.exists()) throw SlotAlreadyTakenException()

                tx.set(docRef, newBooking.toDTO())
            }.await()
            Unit
        }

    // Transacción y no delete() directo: sin conexión, delete() se queda en la cola local y su
    // await() no termina hasta que vuelve la red. Una transacción necesita servidor y falla,
    // así que no queda ningún borrado pendiente que se ejecute más tarde sin que el usuario lo sepa.
    override suspend fun deleteMyBookings(docId: String): ResultManage<Unit, DomainError> =
        firestoreCall {
            val docRef = fireStore.collection(BOOKINGS_COLLECTION).document(docId)

            fireStore.runTransaction { tx ->
                // Si ya no existe (cancelada desde otro dispositivo), el resultado es el mismo
                if (tx.get(docRef).exists()) tx.delete(docRef)
                null
            }.await()
            Unit
        }

    // Borrar la cuenta: todas las reservas del usuario, también las pasadas.
    // Source.SERVER para que, sin red, falle en vez de borrar solo lo que hay en caché.
    // Transacciones por el mismo motivo que en deleteMyBookings; cada una admite 500 escrituras.
    override suspend fun deleteAllUserBookings(userId: String): ResultManage<Unit, DomainError> =
        firestoreCall {
            val refs = fireStore.collection(BOOKINGS_COLLECTION)
                .whereEqualTo(FIELD_USER_ID, userId)
                .get(Source.SERVER)
                .await()
                .documents
                .map { it.reference }

            refs.chunked(MAX_WRITES_PER_TRANSACTION).forEach { chunk ->
                fireStore.runTransaction { tx ->
                    chunk.forEach { tx.delete(it) }
                    null
                }.await()
            }
        }

    override fun getMyBookings(currentUser: String, startAt: Instant): Flow<List<CourtBooking>> =
        fireStore.collection(BOOKINGS_COLLECTION)
            .whereEqualTo(FIELD_USER_ID, currentUser)
            .whereGreaterThan(FIELD_START_AT, Timestamp(startAt))
            .orderBy(FIELD_DATE)
            .observeBookings() //TODO: Estudiar .buffer(Channel.CONFLATED)

    // Obtengo pistas a 7 días vista desde el día actual.
    override fun getBookingsSevenDaysAhead(
        currentDayStart: Instant,
        endSevenDaysFromNow: Instant
    ): Flow<List<CourtBooking>> =
        fireStore.collection(BOOKINGS_COLLECTION)
            .whereGreaterThanOrEqualTo(FIELD_DATE, Timestamp(Date.from(currentDayStart)))
            .whereLessThan(FIELD_DATE, Timestamp(Date.from(endSevenDaysFromNow)))
            .observeBookings()
}

// Ejecuta una operación de Firestore y traduce cualquier fallo a DomainError.
// La cancelación de la corrutina se relanza: no es un error de Firestore.
internal suspend fun <T> firestoreCall(block: suspend () -> T): ResultManage<T, DomainError> =
    try {
        ResultManage.Success(block())
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        ResultManage.Failure(e.toRemoteError())
    }

// Escucha una consulta de reservas en tiempo real. Un error del listener cierra el flow
// con DomainException; al dejar de recolectar se quita el listener.
internal fun Query.observeBookings(): Flow<List<CourtBooking>> = callbackFlow {
    val subscription = addSnapshotListener { snapshot, error ->
        if (error != null) {
            close(DomainException(error.toRemoteError()))
            return@addSnapshotListener
        }
        val bookings = snapshot?.documents?.mapNotNull { doc ->
            doc.toObject(BookingDTO::class.java)?.toDomain()?.copy(id = doc.id)
        }.orEmpty()
        trySend(bookings)
    }
    awaitClose { subscription.remove() }
}

internal fun Exception.toRemoteError(): DomainError =
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

internal class SlotAlreadyTakenException : RuntimeException()

fun NewCourtBooking.toDTO(): NewBookingDTO {
    return NewBookingDTO(
        code = this.code,
        date = Timestamp(this.date),
        hour = this.hour,
        userId = this.userId,
        startsAt = Timestamp(this.startsAt)
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
