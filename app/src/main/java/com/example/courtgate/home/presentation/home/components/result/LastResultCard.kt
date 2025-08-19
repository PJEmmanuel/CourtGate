package com.example.courtgate.home.presentation.home.components.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.courtgate.home.presentation.home.SetResult
import com.example.courtgate.ui.theme.CourtGateTheme


@Composable
fun LastResultCard(
    setResult: List<SetResult>,
    modifier: Modifier = Modifier
) {
    setResult.forEach {
        Card(
            modifier = modifier
                .fillMaxHeight()
                .aspectRatio(0.4f),
            shape = RoundedCornerShape(6.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                Text(text = "${it.firstResult}", style = MaterialTheme.typography.labelLarge)
                HorizontalDivider(thickness = 2.dp)
                Text(text = "${it.secondResult}", style = MaterialTheme.typography.labelLarge)

            }
        }
    }
}

@Preview
@Composable
fun PreviewLast() {
    CourtGateTheme {


        LastResultCard(
            listOf(
                SetResult(firstResult = 3, secondResult = 1),
                SetResult(firstResult = 5, secondResult = 2),
                SetResult(firstResult = 0, secondResult = 4)
            )
        )
    }
}
