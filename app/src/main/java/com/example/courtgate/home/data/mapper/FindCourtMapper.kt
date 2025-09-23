package com.example.courtgate.home.data.mapper

import com.example.courtgate.home.data.remote.CourtBookingDTO
import com.example.courtgate.home.data.remote.CourtListDTO
import com.example.courtgate.home.domain.models.CourtBooking
import com.example.courtgate.home.domain.models.CourtList
import com.google.firebase.Timestamp
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date

fun CourtListDTO.toDomain(): CourtList {
    return CourtList(
        code = this.code.orEmpty(),
        name = this.name.orEmpty(),
        color = this.color.orEmpty(),
        located = this.located.orEmpty(),
        price = this.price ?: 10,
        image = this.image.orEmpty()
    )
}

// Mapper para una lista de objetos
fun List<CourtListDTO>.toDomainList(): List<CourtList> {
    return this.map { it.toDomain() }
}

fun CourtList.toDTO(): CourtListDTO {
    return CourtListDTO(
        code = this.code,
        name = this.name,
        color = this.color,
        located = this.located,
        price = this.price,
        image = this.image
    )
}

fun CourtBookingDTO.toDomain(): CourtBooking {
    val zone = ZoneId.of("Europe/Madrid")
    return CourtBooking(
        code = this.code.orEmpty(),
        // date = this.date.map { it.toZoneDateTime() },
        date = this.date?.toDate()?.toInstant()?.atZone(zone)
            ?: ZonedDateTime.now(zone),
        hour = this.hour.orEmpty(),
        userId = this.userId.orEmpty()
    )
}

// Lista de documentos → lista Domain
fun List<CourtBookingDTO>.toBookingDomainList(): List<CourtBooking> {
    return this.map { it.toDomain() }
}

// Domain → DTO (para enviar a Firestore)
fun CourtBooking.toDTO(): CourtBookingDTO {
    return CourtBookingDTO(
        code = this.code,
        date = Timestamp(Date.from(this.date.toInstant())),
        hour = this.hour,
        userId = this.userId
    )
}