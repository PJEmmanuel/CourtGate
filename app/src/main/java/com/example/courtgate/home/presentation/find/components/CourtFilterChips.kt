package com.example.courtgate.home.presentation.find.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.courtgate.home.domain.models.FilterOption

@Composable
fun CourtFilterChips(
    filters: List<FilterOption>,
    selectedType: String?,
    onLocatedSelected: (String?) -> Unit,
    selectedHour: String?,
    onHourSelected: (String?) -> Unit
) {

    //TODO hay que mirar el hardcode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!SGJPDFBPÑDFMZPOO
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Tipo de pista", style = MaterialTheme.typography.labelMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            filters.forEach { filter ->
                FilterChip(
                    located = filter.located,
                    isSelected = filter.isSelected,
                    onClick = { onLocatedSelected(filter.located) }
                )
            }

            /*Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(label = "Todas", isSelected = selectedType == null) {
                    onTypeSelected(null)
                }
                FilterChip(label = "Indoor", isSelected = selectedType == "Indoor") {
                    onTypeSelected("Indoor")
                }
                FilterChip(label = "Outdoor", isSelected = selectedType == "Outdoor") {
                    onTypeSelected("Outdoor")
                }
            }*/

            /* Spacer(modifier = Modifier.height(8.dp))

             //TODO: Esto hay que sacarlo de aquí y ponerlo en la siguiente pantalla

             Text("Hora", style = MaterialTheme.typography.labelMedium)
             LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                 val hours = listOf("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00")
                 items(hours.size) {
                     FilterChip(label = hours[it], isSelected = selectedHour == it.toString()) {
                         onHourSelected(it.toString())
                     }
                 }
             }*/
        }
    }
}

@Composable
fun FilterChip(
    located: String,
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
            text = located,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}