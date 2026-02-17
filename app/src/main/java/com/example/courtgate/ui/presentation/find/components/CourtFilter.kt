package com.example.courtgate.ui.presentation.find.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.courtgate.R
import com.example.courtgate.ui.presentation.core.CourtFilterChips
import com.example.courtgate.home.domain.models.FilterOption

@Composable
fun CourtFilter(
    filters: List<FilterOption>,
    onLocatedSelected: (String?) -> Unit,
    selectedHour: String?,
    onHourSelected: (String?) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(stringResource(R.string.filter_tittle), style = MaterialTheme.typography.labelMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            filters.forEach { filter ->
                CourtFilterChips(
                    label = filter.located,
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