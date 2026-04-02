package com.example.courtgate.data.datasources

interface SyncPreferencesDataSource {
    fun getLastSyncDay(): String?
    fun saveLastSyncDay(day: String)
}