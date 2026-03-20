package com.example.courtgate.framework

import android.util.Log
import com.example.courtgate.data.datasources.CourtRemoteDataSource
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.framework.remote.BookingDTO
import com.example.courtgate.framework.remote.CourtDTO
import com.example.courtgate.framework.remote.ScheduleDTO
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date
import javax.inject.Inject

const val COURTS_COLLECTION = "courts"
const val BOOKINGS_COLLECTION = "bookings"
const val FIELD_DATE = "date"
const val SETTINGS_COLLECTION = "settings"
const val SCHEDULES_DOCUMENT = "schedules"

class FirebaseFirestoreDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) : CourtRemoteDataSource {

    // Obtengo todas las pistas. NUNCA cambia la oferta de pistas
    override suspend fun getAllCourt(): List<Court> = fireStore
        .collection(COURTS_COLLECTION)
        .get()
        .await().documents
        .mapNotNull { doc ->
            // 1. Convertimos a DTO
            val dto = doc.toObject(CourtDTO::class.java)?.toDomain()
            // 2. Le asignamos el ID del documento de Firestore al DTO
            dto?.copy(id = doc.id)
        }

    //Obtengo los horarios ofertados
    override suspend fun getRegularHours(): List<String> {
        return try {
            val document = fireStore
                .collection(SETTINGS_COLLECTION)
                .document(SCHEDULES_DOCUMENT)
                .get()
                .await()
            val scheduleDto = document.toObject(ScheduleDTO::class.java)
            scheduleDto?.defaultHours ?: emptyList()
        } catch (e: Exception) {
            Log.e("FirebaseFirestoreDataSource", "Error fetching regular hours", e)
            emptyList()
        }
    }
    /* override suspend fun getRegularHours(): List<String> = fireStore
        .collection(SETTINGS_COLLECTION)
        .get()
        .await().documents
        .mapNotNull {
            it.toObject(String::class.java)
        }*/

    //Obtengo pistas a 7 días vista desde el día actual
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
                    doc.toObject(BookingDTO::class.java)?.toDomain()
                }.orEmpty()
                trySend(bookings)
            }
            awaitClose { subscription.remove() }
        }
    }
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

fun List<CourtDTO>.toDomainList(): List<Court> {
    return this.map { it.toDomain() }
}

fun BookingDTO.toDomain(): CourtBooking {
    val zone = ZoneId.of("Europe/Madrid")
    return CourtBooking(
        code = this.code.orEmpty(),
        // date = this.date.map { it.toZoneDateTime() },
        date = this.date?.toDate()?.toInstant()?.atZone(zone)
            ?: ZonedDateTime.now(zone),
        hour = this.hour.orEmpty(),
        userId = this.userId.orEmpty()
    )
}