package com.example.courtgate.framework.database

import androidx.room.RoomDatabase
import androidx.room.migration.Migration

// La 3 es la primera versión con esquema exportado (app/schemas/.../3.json).
const val FIRST_EXPORTED_DB_VERSION = 3

// Versiones anteriores a la 3: solo existieron en desarrollo, sin esquema guardado (la 1 cambió
// varias veces sin subir de versión) y sin ninguna pantalla que creara resultados. No se pueden
// migrar con fiabilidad ni tienen datos del usuario, así que se recrean. Solo afecta a estas dos.
private val PRE_EXPORT_DB_VERSIONS = intArrayOf(1, 2)

// Una Migration(n, n + 1) por cada cambio de esquema a partir de la 3. Nunca destructivas:
// los resultados de Match solo existen en el dispositivo.
val COURT_MIGRATIONS: Array<Migration> = arrayOf()

// Sin fallbackToDestructiveMigration: si falta una migración, Room falla al abrir la base
// en vez de borrarla. CourtMigrationTest comprueba que estén todas.
fun RoomDatabase.Builder<CourtDatabase>.withCourtMigrations(): RoomDatabase.Builder<CourtDatabase> =
    addMigrations(*COURT_MIGRATIONS)
        .fallbackToDestructiveMigrationFrom(true, *PRE_EXPORT_DB_VERSIONS)
