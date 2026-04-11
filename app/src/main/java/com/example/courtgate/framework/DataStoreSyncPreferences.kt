package com.example.courtgate.framework

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.courtgate.data.datasources.SyncPreferencesDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.syncDataStore: DataStore<Preferences> by preferencesDataStore(name = "court_sync")

class DataStoreSyncPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : SyncPreferencesDataSource {

    private companion object {
        val LAST_SYNC_DAY = stringPreferencesKey("last_sync_day")
    }

    override val lastSyncDay: Flow<String?> = context.syncDataStore.data
        .map { preferences -> preferences[LAST_SYNC_DAY] }

    override suspend fun saveLastSyncDay(day: String) {
        context.syncDataStore.edit { preferences ->
            preferences[LAST_SYNC_DAY] = day
        }
    }
}
