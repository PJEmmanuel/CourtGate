package com.example.courtgate.ui.presentation.core

import android.annotation.SuppressLint
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@SuppressLint("ConstantLocale")
private val bookingDateFormatter: DateTimeFormatter =
    DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault())

fun Instant.toBookingDateLabel(): String =
    bookingDateFormatter.format(this)