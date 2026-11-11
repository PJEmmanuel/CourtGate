package com.example.courtgate.framework.database

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Los resultados de Match solo viven en el dispositivo: ninguna actualización puede perderlos.
// Para cada versión exportada, crea una base con un resultado, la abre con la misma
// configuración que la app (withCourtMigrations) y comprueba que el resultado sigue ahí.
@RunWith(AndroidJUnit4::class)
class CourtMigrationTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        CourtDatabase::class.java
    )

    @After
    fun tearDown() {
        context.deleteDatabase(TEST_DB)
    }

    @Test
    fun everyExportedVersion_upgradesToCurrentKeepingResults() {
        for (version in FIRST_EXPORTED_DB_VERSION..CourtDatabase.VERSION) {
            context.deleteDatabase(TEST_DB)
            helper.createDatabase(TEST_DB, version).use { db ->
                db.execSQL(
                    "INSERT INTO last_result (id, set_1_player_1, set_1_player_2, set_2_player_1, " +
                        "set_2_player_2, set_3_player_1, set_3_player_2) VALUES (1, 7, 2, 6, 5, 0, 0)"
                )
            }

            // Valida además que el esquema final coincide con el exportado para VERSION
            helper.runMigrationsAndValidate(TEST_DB, CourtDatabase.VERSION, true, *COURT_MIGRATIONS)
                .close()

            val db = Room.databaseBuilder(context, CourtDatabase::class.java, TEST_DB)
                .withCourtMigrations()
                .build()
            try {
                val results = runBlocking { db.LastResultDAO().getLastResult().first() }
                assertEquals("desde la versión $version", 1, results.size)
                with(results.single()) {
                    assertEquals(listOf(7, 2, 6, 5, 0, 0),
                        listOf(player1Set1, player2Set1, player1Set2, player2Set2, player1Set3, player2Set3))
                }
            } finally {
                db.close()
            }
        }
    }

    // Cada paso n → n+1 desde la primera versión exportada necesita su Migration
    @Test
    fun everyVersionStepHasAMigration() {
        for (from in FIRST_EXPORTED_DB_VERSION until CourtDatabase.VERSION) {
            val found = COURT_MIGRATIONS.any { it.startVersion == from && it.endVersion == from + 1 }
            assertEquals("falta la migración $from → ${from + 1}", true, found)
        }
    }

    private companion object {
        const val TEST_DB = "court-migration-test"
    }
}
