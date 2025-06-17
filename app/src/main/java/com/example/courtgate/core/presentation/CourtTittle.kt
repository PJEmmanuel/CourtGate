package com.example.courtgate.core.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CourtTittle (
    text : String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        color = TODO(), // TODO Mirar el color
        style = MaterialTheme.typography.titleLarge
    )
}