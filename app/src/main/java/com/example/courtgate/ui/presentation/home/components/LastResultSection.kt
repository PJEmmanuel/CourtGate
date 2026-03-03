package com.example.courtgate.ui.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.courtgate.domain.models.LastResult
import com.example.courtgate.ui.presentation.core.CourtTittle
import com.example.courtgate.ui.presentation.home.components.result.LastResultCard

@Composable
fun LastResultSection(
    modifier: Modifier = Modifier,
    lastMatchResult: List<LastResult>
) {
    Column(modifier = modifier) {
        CourtTittle(
            text = "Last result", //TODO: Hardcode
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(16.dp)
        )

        if (lastMatchResult.isEmpty()) {
            EmptyState()
        } else {
            LazyRow(
                modifier = Modifier
                    .height(100.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(lastMatchResult, key = { it.id }) { lastResult ->
                    LastResultCard(
                        setResult = lastResult
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No hay resultados anteriores", //TODO: Hardcode
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
