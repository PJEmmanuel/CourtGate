package com.example.courtgate.data.datasources

import kotlinx.coroutines.flow.Flow

interface SyncPreferencesDataSource {
    val lastSyncDay: Flow<String?>
    suspend fun saveLastSyncDay(day: String)
}
