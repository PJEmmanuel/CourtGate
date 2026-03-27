package com.example.courtgate.framework.database

import androidx.room.Database
import androidx.room.RoomDatabase


//TODO: Configurar la carpeta de esquemas (La recomendada)
//Si quieres que Room guarde los esquemas (muy útil cuando la app crezca y tengas que cambiar tablas
// sin borrar los datos del usuario), debes configurar la ruta en tu archivo build.gradle.kts
// (Module :app).
//Como estás usando KSP (según el log del error), añade esto dentro del bloque android:
//Kotlin
//// En app/build.gradle.kts
//android {
//    // ... resto de tu configuración ...
//
//    ksp {
//        arg("room.schemaLocation", "$projectDir/schemas")
//    }
//}
//Y asegúrate de que el plugin de Room esté aplicado arriba en el mismo archivo:
//Kotlin
//plugins {
//    // ...
//    id("androidx.room") version "2.7.1" // Usa la versión que tengas instalada
//}

@Database(
    entities = [
        LastResultEntity::class,
        CourtEntity::class,
        BookingEntity::class,
        ScheduleEntity::class
               ],
    version = 3,
    exportSchema = false
)
abstract class CourtDatabase : RoomDatabase() {
    abstract fun LastResultDAO(): LastResultDAO
    abstract fun ManageCourtDAO(): ManageCourtDAO
}