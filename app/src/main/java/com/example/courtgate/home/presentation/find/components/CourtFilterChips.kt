package com.example.courtgate.home.presentation.find.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CourtFilterChips(
    selectedType: String?,
    onTypeSelected: (String?) -> Unit,
    selectedHour: String?,
    onHourSelected: (String?) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Tipo de pista", style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(label = "Todas", isSelected = selectedType == null) {
                onTypeSelected(null)
            }
            FilterChip(label = "Indoor", isSelected = selectedType == "indoor") {
                onTypeSelected("indoor")
            }
            FilterChip(label = "Outdoor", isSelected = selectedType == "outdoor") {
                onTypeSelected("outdoor")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Hora", style = MaterialTheme.typography.labelMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val hours = listOf("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00")
            items(hours.size) {
                FilterChip(label = hours[it], isSelected = selectedHour == it.toString()) {
                    onHourSelected(it.toString())
                }
            }
        }
    }
}

@Composable
fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (isSelected) 2.dp else 0.dp,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/*@Preview
@Composable
fun Showfilter() {
    FilterChip(
        label = "08:00",
        isSelected = true
    ) { }
}

@Preview
@Composable
fun ShowfilterChips() {
    CourtFilterChips(
        selectedType = "Indoor",
        onTypeSelected = {},
        selectedHour = "10:30"
    ) { }
}*/