package com.example.courtgate.home.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.courtgate.core.presentation.CourtTittle
import com.example.courtgate.home.presentation.home.LastMatchResult
import com.example.courtgate.home.presentation.home.components.result.LastResultCard

@Composable
fun LastResult(
    lastMatchResult: List<LastMatchResult>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        CourtTittle(
            text = "Last result",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(16.dp)
        )
        LazyRow(
            modifier = Modifier
                .height(100.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(lastMatchResult.size) {
                LastResultCard(
                    setResult = lastMatchResult[it].match
                )
            }
        }
    }
}
