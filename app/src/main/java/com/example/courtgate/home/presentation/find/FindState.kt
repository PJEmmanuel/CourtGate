package com.example.courtgate.home.presentation.find

import com.example.courtgate.home.domain.models.CourtList
import java.time.ZonedDateTime

data class FindState(
    val courtList: List<CourtList> = emptyList(),
    val selectedHour: String = "",
    val selectedType: String = "",
    val onHourSelected: String = "",
    val mainDate: ZonedDateTime = ZonedDateTime.now()
)
