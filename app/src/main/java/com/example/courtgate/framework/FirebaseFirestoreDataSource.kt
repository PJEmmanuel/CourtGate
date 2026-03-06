package com.example.courtgate.framework

import android.util.Log
import com.example.courtgate.ResultCourt
import com.example.courtgate.data.datasources.CourtRemoteDataSource
import com.example.courtgate.domain.models.CourtBooking
import com.example.courtgate.domain.models.CourtList
import com.example.courtgate.framework.remote.CourtBookingDTO
import com.example.courtgate.framework.remote.CourtListDTO
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

class FirebaseFirestoreDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) : CourtRemoteDataSource {
    override suspend fun getAllCourtToShow(): ResultCourt<List<CourtList>> {
        return try {
            val dtoList = fireStore.collection("courts")//TODO Hardcoded!!
                .get()
                .await().documents
                .mapNotNull {
                    it.toObject(CourtListDTO::class.java)
                }
            ResultCourt.Success(dtoList.toDomainList())
        } catch (e: Exception) {
            ResultCourt.Error(e)
        }
    }

    override suspend fun getCourtSelectedByCode(code: String): ResultCourt<CourtList> {
        return try {
            val snapshot = fireStore.collection("courts")
                .whereEqualTo("code", code)
                .limit(1)
                .get()
                .await()

            val dtoCourt = snapshot.documents.firstOrNull()?.toObject(CourtListDTO::class.java)

            if (dtoCourt != null) {
                ResultCourt.Success(dtoCourt.toDomain())
            } else {
                ResultCourt.Error(Exception("Court not found with code=$code")) //TODO: manejo del error y hardcode
            }

        } catch (e: Exception) {
            Log.e("HomeRepositoryImpl", "Error fetching court by code", e)
            ResultCourt.Error(e)
        }
    }

    override fun getBookingsByDate(
        startOfDay: Instant,
        endOfDay: Instant
    ): Flow<ResultCourt<List<CourtBooking>>> = callbackFlow {

        val query = fireStore.collection("bookings")//TODO: hardcode
            .whereGreaterThanOrEqualTo("date", Timestamp(Date.from(startOfDay)))
            .whereLessThan("date", Timestamp(Date.from(endOfDay)))

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(ResultCourt.Error(error))
                return@addSnapshotListener
            }

            val bookings = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(CourtBookingDTO::class.java)?.toDomain()
            }.orEmpty()

            trySend(ResultCourt.Success(bookings))
        }
        // Importante: Cierra el listener cuando el Flow se cancela para evitar fugas de memoria
        awaitClose { subscription.remove() }
    }

    override fun getFreeHoursOnReservedCourts(
        code: String,
        startOfDay: Instant,
        endOfDay: Instant
    ): Flow<ResultCourt<List<CourtBooking>>> = callbackFlow {

        val query = fireStore.collection("bookings") //TODO: hardcode
            .whereEqualTo("code", code)
            .whereGreaterThanOrEqualTo("date", Timestamp(Date.from(startOfDay)))
            .whereLessThan("date", Timestamp(Date.from(endOfDay)))

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(ResultCourt.Error(error))
                return@addSnapshotListener
            }
            val bookings = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(CourtBookingDTO::class.java)?.toDomain()
            }.orEmpty()

            trySend(ResultCourt.Success(bookings))
        }
        awaitClose { subscription.remove() }
    }
}

fun CourtListDTO.toDomain(): CourtList {
    return CourtList(
        code = this.code.orEmpty(),
        name = this.name.orEmpty(),
        color = this.color.orEmpty(),
        located = this.located.orEmpty(),
        price = this.price ?: 10,
        image = this.image.orEmpty()
    )
}

// Mapper para una lista de objetos
fun List<CourtListDTO>.toDomainList(): List<CourtList> {
    return this.map { it.toDomain() }
}

fun CourtList.toDTO(): CourtListDTO {
    return CourtListDTO(
        code = this.code,
        name = this.name,
        color = this.color,
        located = this.located,
        price = this.price,
        image = this.image
    )
}

fun CourtBookingDTO.toDomain(): CourtBooking {
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

// Lista de documentos → lista Domain
fun List<CourtBookingDTO>.toBookingDomainList(): List<CourtBooking> {
    return this.map { it.toDomain() }
}

// Domain → DTO (para enviar a Firestore)
fun CourtBooking.toDTO(): CourtBookingDTO {
    return CourtBookingDTO(
        code = this.code,
        date = Timestamp(Date.from(this.date.toInstant())),
        hour = this.hour,
        userId = this.userId
    )
}