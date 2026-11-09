package com.example.courtgate.ui.presentation.myBookings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.courtgate.domain.models.CourtBooking

@Composable
fun MyBookingsList(
    modifier: Modifier = Modifier,
    courts: List<CourtBooking>,
    onCourtClick: (CourtBooking) -> Unit,
) {

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Center
    )
    {
        items(items = courts, key = { it.id }) { court ->
            MyBookingsCard(
                court = court,
                onClick = { onCourtClick(court) }
            )
        }
    }
}