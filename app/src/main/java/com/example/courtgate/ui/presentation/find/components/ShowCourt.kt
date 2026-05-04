package com.example.courtgate.ui.presentation.find.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.DomainError
import com.example.courtgate.ui.presentation.core.ErrorScreen

@Composable
fun ShowCourt(
    modifier: Modifier = Modifier,
    courts: List<Court>,
    onCourtClick: (Court) -> Unit,
) {

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Center
    )
    {
        items(items = courts, key = { it.id }) { court ->
            ShowCourtCard(
                modifier = Modifier,
                court = court,
                onClick = { onCourtClick(court) }
            )
        }
    }
}