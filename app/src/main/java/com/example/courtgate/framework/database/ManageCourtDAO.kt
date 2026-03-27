package com.example.courtgate.framework.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ManageCourtDAO {

    /* Fijas */

    // Insertar en una llamada las pistas que hay a Room
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCourt(courtEntity: List<CourtEntity>)

    // Inserta la referencia del horario ofertado
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegularHours(scheduleEntity: List<ScheduleEntity>)

    // Inserta reservas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBookings(courtBooking: List<BookingEntity>)

    // Para saber si court está vacío en el repositorio
    @Query("SELECT COUNT(*) FROM courts")
    suspend fun getCourtCount(): Int

    // Para saber si horario está vacío
    @Query("SELECT COUNT(*) FROM schedules")
    suspend fun getScheduleCount(): Int

    @Transaction
    suspend fun syncBookingsForDay(
        windowStart: Long,
        windowEnd: Long,
        bookings: List<BookingEntity>
    ) {
        deleteBookingsByDay(windowStart, windowEnd)
        insertAllBookings(bookings)
    }

    // Borrado de un día concreto entero
    @Query("DELETE FROM bookings WHERE date >= :windowStart AND date < :windowEnd")
    suspend fun deleteBookingsByDay(windowStart: Long, windowEnd: Long)


    /*Para pantalla FindCourt!!!*/

    /*Llama a todas las pistas de un día concreto, que AL MENOS tengan un tramo horario libre
    hay que pasarle la fecha del día de la consulta, esto hace que compare las reserevas
    a ver si hay alguna hora disponible del día concreto que se le mande.
    Si no hay ninguna hora libre, no se muestra esa pista*/
    @Query(
        """
        SELECT * FROM courts
WHERE (:locatedFilter IS NULL OR located = :locatedFilter)
AND (
    (SELECT COUNT(*) FROM schedules) >
    (
        SELECT COUNT(*) FROM bookings
        WHERE bookings.code = courts.code
        AND bookings.date >= :selectedDay
        AND bookings.date < :endSelectedDay
    )
)

    """
    )
    fun getAvailableCourts(
        locatedFilter: String?,
        selectedDay: Long,
        endSelectedDay: Long
    ): Flow<List<CourtEntity>>

    @Query("SELECT DISTINCT located FROM courts")
    suspend fun getDistinctLocatedTypes(): List<String>


    /*Para pantalla BookNow!!!*/

    // Llamar a una pista en concreto.
    // TODO: Necesito las horas libres de esta pista para mostrarlas al usuario
    // Para ello tiene que estar escuchando las reservas continuamente.
    /*  @Query("SELECT *")
      fun getCourtByCode() // Flow
  */
    /*Para el apartado de modificar reservas!!!*/


}
