package com.example.courtgate.ui.presentation.booking.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.courtgate.ui.presentation.core.CourtFilterChips
import com.example.courtgate.domain.models.FreeHoursOfCourt

@Composable
fun BookingFilterChip(
    selectedHour: String?,
    freeHoursList: List<FreeHoursOfCourt>,
    onHourSelected: (String) -> Unit,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(freeHoursList, {it.hour}) {freeHours ->
            CourtFilterChips(
                label = freeHours.hour,
                isSelected = freeHours.hour == selectedHour,
                onClick = { onHourSelected(freeHours.hour) },
                isFree = freeHours.isFree
            )
        }
    }
}