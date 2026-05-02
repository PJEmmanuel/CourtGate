package com.example.courtgate.framework.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.courtgate.domain.models.Court
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

    @Query("SELECT DISTINCT located FROM courts")
    fun getDistinctLocatedTypes(): Flow<List<String>>

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


    /*Para pantalla BookNow!!!*/


    @Query("""
    SELECT s.hour,
           CASE WHEN b.hour IS NULL THEN 1 ELSE 0 END AS isFree
    FROM schedules s
    LEFT JOIN bookings b 
        ON s.hour = b.hour 
        AND b.code = :code 
        AND b.date >= :dayStart 
        AND b.date < :dayEnd
    ORDER BY s.hour
""")
    fun getHoursWithAvailability(
        code: String,
        dayStart: Long,
        dayEnd: Long
    ): Flow<List<CourtHourAvailability>>


    @Query("SELECT * FROM courts WHERE code = :code LIMIT 1")
    fun getCourtByCode(code: String): Flow<CourtEntity>


    /*Para el apartado de modificar reservas!!!*/


}
