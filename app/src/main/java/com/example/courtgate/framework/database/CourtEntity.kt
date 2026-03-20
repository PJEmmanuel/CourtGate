package com.example.courtgate.framework.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courts")
data class CourtEntity(
    @PrimaryKey
    val id: String, // Usamos el ID del documento de Firestore

    @ColumnInfo(name = "code")
    val code: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "color")
    val color: String,

    @ColumnInfo(name = "image")
    val image: String,

    @ColumnInfo(name = "located")
    val located: String,

    @ColumnInfo(name = "price")
    val price: Int
)

/*fun CourtListDTO.toEntity(docId: String): CourtEntity {
    return CourtEntity(
        id = docId,
        code = this.code ?: "",
        name = this.name ?: "",
        color = this.color ?: "",
        image = this.image ?: "",
        located = this.located ?: "",
        price = this.price ?: 0
    )
}*/