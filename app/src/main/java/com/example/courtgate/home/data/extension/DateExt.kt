package com.example.courtgate.home.data.extension

import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZonedDateTime

fun Timestamp.toZoneDateTime(): ZonedDateTime{
    return ZonedDateTime.now()
}

fun ZonedDateTime.toTimestamp(): Timestamp {
   return this.toTimestamp()
}