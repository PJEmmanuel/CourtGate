package com.example.courtgate.framework.database

import androidx.room.Database
import androidx.room.RoomDatabase

// Esquemas exportados en app/schemas (los genera la compilación). Al cambiar una entidad:
// subir VERSION, añadir su Migration en CourtMigrations.kt y commitear el JSON nuevo.
@Database(
    entities = [
        LastResultEntity::class,
        CourtEntity::class,
        BookingEntity::class,
        ScheduleEntity::class
               ],
    version = CourtDatabase.VERSION,
    exportSchema = true
)
abstract class CourtDatabase : RoomDatabase() {
    abstract fun LastResultDAO(): LastResultDAO
    abstract fun ManageCourtDAO(): ManageCourtDAO

    companion object {
        const val VERSION = 3
        const val NAME = "court_db"
    }
}
