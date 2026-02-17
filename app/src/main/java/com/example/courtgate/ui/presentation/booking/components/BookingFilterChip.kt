package com.example.courtgate.ui.presentation.booking.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.courtgate.ui.presentation.core.CourtFilterChips
import com.example.courtgate.home.domain.models.FreeHoursOfCourt

@Composable
fun BookingFilterChip(
    freeHoursList: List<FreeHoursOfCourt>,
    timeOffer: List<String>,
    onHourSelected: (String) -> Unit,
) {

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(freeHoursList.size) {
            CourtFilterChips(
                label = freeHoursList[it].hour,
                isSelected = freeHoursList[it].isSelected, // TODO: Gestionar la seleccion de horas
                onClick = { onHourSelected(freeHoursList[it].hour) }, //TODO: comprobar que hay que mandar
                isFree = !freeHoursList[it].isFree
            )
        }
    }
}