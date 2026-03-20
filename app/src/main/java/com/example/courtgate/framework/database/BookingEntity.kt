package com.example.courtgate.framework.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey
    val id: String, // ID del documento de Firestore

    @ColumnInfo(name = "code")
    val code: String,

    @ColumnInfo(name = "date")
    val date: Long, // Guardamos el timestamp en milisegundos para Room

    @ColumnInfo(name = "hour")
    val hour: String,

    @ColumnInfo(name = "user_id")
    val userId: String
)

/*fun CourtBookingDTO.toEntity(docId: String): BookingEntity {
    return BookingEntity(
        id = docId,
        code = this.code ?: "",
        date = this.date?.toDate()?.time ?: 0L, // Convierte Timestamp a Long
        hour = this.hour ?: "",
        userId = this.userId ?: ""
    )
}*/