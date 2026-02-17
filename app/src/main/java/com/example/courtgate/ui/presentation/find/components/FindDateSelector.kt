package com.example.courtgate.ui.presentation.find.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.ZonedDateTime

@Composable
fun FindDateSelector(
    modifier: Modifier = Modifier,
    dateToShow: Int = 7,
    mainDate: ZonedDateTime,
    selectedDate: ZonedDateTime,
    onDateClick: (ZonedDateTime) -> Unit,
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(count = dateToShow) {
            val currentDate = mainDate.plusDays(it.toLong())
            FindDateItem(
                modifier = Modifier,
                date = currentDate,
                onClick = { onDateClick(currentDate) },
                isSelected = selectedDate.toLocalDate() == currentDate.toLocalDate()
            )
        }
    }
}

/*@Preview
@Composable

fun PreviewFind(){
    FindDateSelector(
        mainDate = ZonedDateTime.now(),
        onDateClick = {},
    )
}*/