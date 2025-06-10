package com.example.courtgate.core.presentation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CourtButton(
    text: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelMedium)
    }
}
