package com.example.courtgate.ui.presentation.core

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun CourtButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    text: String,
    shape: Shape,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = isEnabled,
        shape = shape,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else Text(text = text, style = MaterialTheme.typography.labelMedium)
    }
}
