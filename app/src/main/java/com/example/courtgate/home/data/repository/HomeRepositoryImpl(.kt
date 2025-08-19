package com.example.courtgate.home.data.repository

import com.example.courtgate.home.data.local.LastResultDAO
import com.example.courtgate.home.data.local.entity.LastResultEntity
import com.example.courtgate.home.data.mapper.toDomain
import com.example.courtgate.home.data.mapper.toEntity
import com.example.courtgate.home.domain.models.LastResult
import com.example.courtgate.home.domain.repository.HomeRepository

class HomeRepositoryImpl(private val dao: LastResultDAO) : HomeRepository {

    override fun insertLastResult(lastResult: LastResult) {
        return dao.insertLastResult(lastResult.toEntity())
    }

    override  fun getLastResult(): List<LastResult> {
        return dao.getLastResult().map { it.toDomain() }
    }
}