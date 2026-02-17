package com.example.courtgate.ui.presentation.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.EventBusy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CourtFilterChips(
    label: String,
    isSelected: Boolean,
    isFree: Boolean = true,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        !isFree -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when {
        !isFree -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        isSelected -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        tonalElevation = if (isSelected) 2.dp else 0.dp,
        modifier = Modifier
            .padding(4.dp)
            .then(
                if (isFree)
                    Modifier.clickable { onClick() }
                else
                    Modifier // sin clickable si está ocupado
            )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!isFree) {
                Text(
                    text = label,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    modifier = Modifier
                        .alpha(0.4f)
                        .padding(top = 4.dp, bottom = 4.dp, end = 8.dp),
                    imageVector = Icons.TwoTone.EventBusy,
                    contentDescription = "is Free"
                )
            } else {
                Text(
                    text = label,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
