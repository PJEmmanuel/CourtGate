package com.example.courtgate.ui.presentation.match.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.courtgate.R
import com.example.courtgate.domain.models.LastResult
import com.example.courtgate.ui.presentation.home.components.result.LastResultCard

@Composable
fun MatchResultList(
    results: List<LastResult>,
    onEdit: (LastResult) -> Unit,
    onDelete: (LastResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (results.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.match_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        // Margen inferior para que el FAB no tape el último resultado
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(results, key = { it.id }) { result ->
            MatchResultItem(
                result = result,
                onEdit = { onEdit(result) },
                onDelete = { onDelete(result) }
            )
        }
    }
}

@Composable
private fun MatchResultItem(
    result: LastResult,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onEdit),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LastResultCard(setResult = result)
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onEdit) {
            Icon(
                imageVector = Icons.TwoTone.Edit,
                contentDescription = stringResource(R.string.match_edit_result)
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.TwoTone.Delete,
                contentDescription = stringResource(R.string.match_delete_result)
            )
        }
    }
}
