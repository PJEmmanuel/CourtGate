package com.example.courtgate.fakes

import com.example.courtgate.data.datasources.SyncPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// En memoria para no escribir en el DataStore de la app instalada
@Singleton
class FakeSyncPreferences @Inject constructor() : SyncPreferencesDataSource {
    private val day = MutableStateFlow<String?>(null)
    override val lastSyncDay: Flow<String?> = day
    override suspend fun saveLastSyncDay(day: String) {
        this.day.value = day
    }
}
