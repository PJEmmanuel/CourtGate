package com.example.courtgate.home.data.repository

import android.util.Log
import com.example.courtgate.home.data.local.LastResultDAO
import com.example.courtgate.home.data.mapper.toDomain
import com.example.courtgate.home.data.mapper.toDomainList
import com.example.courtgate.home.data.mapper.toEntity
import com.example.courtgate.home.data.remote.CourtListDTO
import com.example.courtgate.home.domain.models.CourtList
import com.example.courtgate.home.domain.models.LastResult
import com.example.courtgate.home.domain.repository.HomeRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class HomeRepositoryImpl(private val dao: LastResultDAO) : HomeRepository {

    //HomeScreen
    override fun insertLastResult(lastResult: LastResult) {
        return dao.insertLastResult(lastResult.toEntity())
    }

    override fun getLastResult(): List<LastResult> {
        return dao.getLastResult().map { it.toDomain() }
    }

    //FindCourtScreen
    override suspend fun getAllCourtToShow(): List<CourtList> {
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
    }
}