package com.example.courtgate.data

import com.example.courtgate.data.datasources.CourtLocalDataSource
import com.example.courtgate.data.datasources.CourtRemoteDataSource
import com.example.courtgate.di.ApplicationScope
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.FilterOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

class ManageCourtRepository @Inject constructor(
    private val remoteDataSource: CourtRemoteDataSource,
    private val localDataSource: CourtLocalDataSource,
    @ApplicationScope private val externalScope: CoroutineScope
) {

    /* fun getAllCourtToShow(
         located: String?,
         selectedDay: Long,
         endSelectedDay: Long,
         currentDayStart: Instant,
         endSevenDaysFromNow: Instant
     ): Flow<List<Court>> = flow {

         // Disparamos la sincronización en segundo plano sin bloquear el flujo
         // Esto asegura que la UI vea lo que hay en Room inmediatamente
         coroutineScope {
             launch {
                 try {
                     syncRemoteData(
                         selectedDay = selectedDay,
                         endSelectedDay = endSelectedDay,
                         currentDayStart = currentDayStart,
                         endSevenDaysFromNow = endSevenDaysFromNow
                     )
                 } catch (e: Exception) { //TODO:Hay que mandar el error!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                     // Loguear error de red, pero no romper el Flow de la DB
                 }
             }
         }
         // Retornamos el stream de la base de datos (SSOT)
         emitAll(localDataSource.getAvailableCourts(located, selectedDay, endSelectedDay))
     }.flowOn(Dispatchers.IO)//TODO NEcesario flowOn?*/
    fun getAllCourtToShow(
        located: String?,
        selectedDay: Long,
        endSelectedDay: Long,
        currentDayStart: Instant,
        endSevenDaysFromNow: Instant
    ): Flow<List<Court>> {

        // Lanzamos la sincronización en el scope externo
        externalScope.launch {
            try {
                syncRemoteData(
                    selectedDay,
                    endSelectedDay,
                    currentDayStart,
                    endSevenDaysFromNow
                )
            } catch (e: Exception) {
                // Loguear error, la UI no se rompe porque ya está escuchando Room
            }
        }

        /* Retornamos directamente el flujo de Room.
         Cuando syncRemoteData actualice Room, este Flow emitirá automáticamente.*/
        return localDataSource.getAvailableCourts(located, selectedDay, endSelectedDay)
            .distinctUntilChanged() // evitará emisiones si los datos son idénticos.
            .flowOn(Dispatchers.IO)
    }

    suspend fun getFilterOption(): List<FilterOption> = localDataSource.getDistinctLocatedTypes()

    private suspend fun ensureStaticDataLoaded() {
        val remoteCourt = remoteDataSource.getAllCourt()
        localDataSource.saveAllCourt(remoteCourt)

        val schedule = remoteDataSource.getRegularHours()
        localDataSource.saveRegularHours(schedule)
    }

    private suspend fun syncRemoteData(
        selectedDay: Long,
        endSelectedDay: Long,
        currentDayStart: Instant,
        endSevenDaysFromNow: Instant
    ) {
        ensureStaticDataLoaded()
        // Sincronizamos reservas de Firestore a Room
        remoteDataSource.getBookingsSevenDaysAhead(
            currentDayStart = currentDayStart,
            endSevenDaysFromNow = endSevenDaysFromNow
        ).collect { bookings ->
            localDataSource.syncBookingsForDay(
                selectedDay = selectedDay,
                endSelectedDay = endSelectedDay,
                bookings = bookings
            )
        }
    }
}