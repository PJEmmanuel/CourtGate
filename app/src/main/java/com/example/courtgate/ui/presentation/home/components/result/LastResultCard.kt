package com.example.courtgate.ui.presentation.home.components.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.courtgate.domain.models.LastResult
import com.example.courtgate.ui.theme.CourtGateTheme

@Composable
fun LastResultCard(
    setResult: LastResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxHeight()
            .aspectRatio(if (setResult.player1Set3 != 0 || setResult.player2Set3 != 0) 1.8f else 1.2f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            SetColumn(
                p1 = setResult.player1Set1,
                p2 = setResult.player2Set1,
                modifier = Modifier.weight(1f)
            )

            VerticalDivider(thickness = 1.dp, modifier = Modifier.fillMaxHeight(0.6f))

            SetColumn(
                p1 = setResult.player1Set2,
                p2 = setResult.player2Set2,
                modifier = Modifier.weight(1f)
            )

            if (setResult.player1Set3 != 0 || setResult.player2Set3 != 0) {
                VerticalDivider(thickness = 1.dp, modifier = Modifier.fillMaxHeight(0.6f))
                SetColumn(
                    p1 = setResult.player1Set3,
                    p2 = setResult.player2Set3,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SetColumn(
    p1: Int,
    p2: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = p1.toString(),
            style = MaterialTheme.typography.labelLarge
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.5f), // Línea más corta para que quede elegante
            thickness = 2.dp
        )
        Text(
            text = p2.toString(),
            style = MaterialTheme.typography.labelLarge
        )
    }
}


@Preview
@Composable
fun PreviewLast() {
    CourtGateTheme {
        LastResultCard(
            LastResult(
                id = 1,
                6,
                1,
                4,
                6,
                6,
                3
            )
        )
    }
}
