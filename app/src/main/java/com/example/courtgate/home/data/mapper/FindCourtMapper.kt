package com.example.courtgate.home.data.mapper

import com.example.courtgate.home.data.remote.CourtListDTO
import com.example.courtgate.home.domain.models.CourtList

fun CourtListDTO.toDomain(): CourtList {
    return CourtList(
        name = this.name ?: "No name",
        color = this.color ?: "Random",
        located = this.located?: "No Data",
        price = this.price?: 10,
        image = this.image?: "No Data"
    )
}

// Mapper para una lista de objetos
fun List<CourtListDTO>.toDomainList(): List<CourtList> {
    return this.map { it.toDomain() }
}

fun CourtList.toDTO(): CourtListDTO {
    return CourtListDTO(
        name = this.name,
        color = this.color,
        located = this.located,
        price = this.price,
        image = this.image
    )
}