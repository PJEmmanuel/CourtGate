package com.example.courtgate.framework

import android.content.Context
import com.example.courtgate.data.datasources.SyncPreferencesDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.core.content.edit

private const val PREFS_NAME = "court_sync"
private const val KEY_LAST_SYNC_DAY = "last_sync_day"

class SharedPrefsSyncPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
): SyncPreferencesDataSource {
    
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getLastSyncDay(): String? =
        prefs.getString(KEY_LAST_SYNC_DAY, null)

    override fun saveLastSyncDay(day: String) {
        prefs.edit { putString(KEY_LAST_SYNC_DAY, day) }
    }
}
