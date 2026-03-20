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
import com.example.courtgate.domain.models.FilterOption

@Composable
fun CourtFilter(
    filters: List<FilterOption>,
    onLocatedSelected: (String?) -> Unit,
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
        }
    }
}