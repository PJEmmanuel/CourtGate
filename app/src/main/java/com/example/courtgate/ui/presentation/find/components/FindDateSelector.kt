package com.example.courtgate.ui.presentation.find.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.ZonedDateTime

@Composable
fun FindDateSelector(
    modifier: Modifier = Modifier,
    dateRange: List<ZonedDateTime>,
    selectedDate: ZonedDateTime,
    onDateClick: (ZonedDateTime) -> Unit,
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = dateRange) { date ->
            FindDateItem(
                date = date,
                onClick = { onDateClick(date) },
                isSelected = selectedDate.toLocalDate() == date.toLocalDate(),
            )
        }
    }
}