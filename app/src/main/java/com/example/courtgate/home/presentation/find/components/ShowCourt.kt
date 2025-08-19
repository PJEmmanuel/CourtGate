package com.example.courtgate.home.presentation.find.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.courtgate.home.domain.models.CourtList

@Composable
fun ShowCourt(
    modifier: Modifier = Modifier,
    courtList: List<CourtList>,
    onCourtClick: (CourtList) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Center
    )
    {
        items(count = courtList.size) {

            ShowCourtCard(
                modifier = Modifier,
                court = courtList[it],
                onClick = { onCourtClick } //TODO mandar la pista selec...
            )
        }
    }
}