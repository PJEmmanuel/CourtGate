package com.example.courtgate.home.data.repository

import android.util.Log
import com.example.courtgate.home.data.local.LastResultDAO
import com.example.courtgate.home.data.mapper.toBookingDomainList
import com.example.courtgate.home.data.mapper.toDomain
import com.example.courtgate.home.data.mapper.toDomainList
import com.example.courtgate.home.data.mapper.toEntity
import com.example.courtgate.home.data.remote.CourtBookingDTO
import com.example.courtgate.home.data.remote.CourtListDTO
import com.example.courtgate.home.domain.models.CourtBooking
import com.example.courtgate.home.domain.models.CourtList
import com.example.courtgate.home.domain.models.LastResult
import com.example.courtgate.home.domain.repository.HomeRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.ZonedDateTime
import java.util.Date

class HomeRepositoryImpl(private val dao: LastResultDAO) : HomeRepository {

    //HomeScreen
    override fun insertLastResult(lastResult: LastResult) {
        return dao.insertLastResult(lastResult.toEntity())
    }

    override fun getLastResult(): List<LastResult> {
        return dao.getLastResult().map { it.toDomain() }
    }

    //FindCourtScreen
    /* override suspend fun getAllCourtToShow(): List<CourtList> {
         return try {
             val dtoList = Firebase.firestore.collection("courts")//TODO Hardcoded!!
                 .get()
                 .await().documents
                 .mapNotNull {
                     it.toObject(CourtListDTO::class.java)
                 }
             dtoList.toDomainList()
         } catch (e: Exception) {
             Log.e("HomeRepositoryImpl", "Error fetching courts from Firestore", e)
             emptyList()
         }
     }*/

    override suspend fun getAllCourtToShow(): Result<List<CourtList>> {
        return try {
            val dtoList = Firebase.firestore.collection("courts")//TODO Hardcoded!!
                .get()
                .await().documents
                .mapNotNull {
                    it.toObject(CourtListDTO::class.java)
                }
            Result.success(dtoList.toDomainList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCourtSelectedByCode(code: String): Result<CourtList> {
        return try {
            val snapshot = Firebase.firestore.collection("courts")
                .whereEqualTo("code", code)
                .limit(1)
                .get()
                .await()

            val dtoCourt = snapshot.documents.firstOrNull()?.toObject(CourtListDTO::class.java)

            if (dtoCourt != null) {
                Result.success(dtoCourt.toDomain())
            } else {
                Result.failure(Exception("Court not found with code=$code"))
            }

        } catch (e: Exception) {
            Log.e("HomeRepositoryImpl", "Error fetching court by code", e)
            Result.failure(e)
        }
    }

    override suspend fun getBookingsByDate(
        startOfDay: Instant,
        endOfDay: Instant
    ): Result<List<CourtBooking>> {
        return try {
            val dtoBookingList = Firebase.firestore.collection("bookings")
                .whereGreaterThanOrEqualTo("date", Timestamp(Date.from(startOfDay)))
                .whereLessThan("date", Timestamp(Date.from(endOfDay)))
                .get()
                .await().documents
                .mapNotNull {
                    it.toObject(CourtBookingDTO::class.java)
                }
            Result.success(dtoBookingList.toBookingDomainList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFreeHoursOnReservedCourts(
        code: String,
        startOfDay: Instant,
        endOfDay: Instant
    ): Result<List<CourtBooking>> {

        return try {
            val dtoBooking = Firebase.firestore.collection("bookings")
                .whereEqualTo("code", code)
                .whereGreaterThanOrEqualTo("date", Timestamp(Date.from(startOfDay)))
                .whereLessThan("date", Timestamp(Date.from(endOfDay)))
                .get()
                .await().documents
                .mapNotNull {
                    it.toObject(CourtBookingDTO::class.java)
                }
            Result.success(dtoBooking.toBookingDomainList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}