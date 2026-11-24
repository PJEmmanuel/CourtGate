package com.example.courtgate.ui.presentation.core

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.courtgate.R
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.CourtBooking

// Lo que pinta CourtCard, ya con los textos resueltos. Cada pantalla construye el suyo
// a partir de su modelo de dominio (Court en Find, CourtBooking en Mis reservas).
@Immutable
data class CourtCardUiModel(
    val title: String,
    val lines: List<String>,
    val highlight: String? = null,
    val image: CourtCardImage? = null,
)

@Immutable
data class CourtCardImage(
    @param:DrawableRes val res: Int,
    val contentDescription: String,
)

@Composable
fun Court.toCardUiModel(): CourtCardUiModel = CourtCardUiModel(
    title = name,
    lines = listOf(stringResource(R.string.court_color_label, color), located),
    highlight = stringResource(R.string.court_price_format, price.toString()),
    image = CourtCardImage(
        res = R.drawable.avatar,
        contentDescription = stringResource(R.string.court_image_description, name)
    ),
)

@Composable
fun CourtBooking.toCardUiModel(): CourtCardUiModel = CourtCardUiModel(
    title = code,
    lines = listOf(
        stringResource(R.string.booking_date_label, date.toBookingDateLabel()),
        stringResource(R.string.booking_hour_label, hour),
    ),
)

@Composable
fun CourtCard(
    model: CourtCardUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = model.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                model.lines.forEach { line ->
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                model.highlight?.let { highlight ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = highlight,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            model.image?.let { image ->
                Image(
                    painter = painterResource(image.res),
                    contentDescription = image.contentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }
    }
}
