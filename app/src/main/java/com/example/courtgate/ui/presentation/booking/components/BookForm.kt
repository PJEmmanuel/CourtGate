package com.example.courtgate.ui.presentation.booking.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ColorLens
import androidx.compose.material.icons.twotone.EuroSymbol
import androidx.compose.material.icons.twotone.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.courtgate.ui.presentation.core.CourtButton
import com.example.courtgate.domain.models.Court
import com.example.courtgate.domain.models.FreeHoursOfCourt

@Composable
fun BookForm(
    modifier: Modifier = Modifier,
    freeHoursList: List<FreeHoursOfCourt>,
    court: Court?,
    selectedHour: String?,
    isLoading: Boolean = false,
    onHourSelected: (String) -> Unit,
    onBookClicked: () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Nombre de la pista
            Text(
                text = court?.name ?: "N/A",
                style = MaterialTheme.typography.headlineSmall, // más grande que titleLarge
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary, // le das tu color principal
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp)
                    .align(Alignment.Start) // alinear a la izquierda, más natural
            )

            //  Spacer(modifier = Modifier.height(6.dp))

            // Chips decorativos
            Row(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoChip(text = court?.name ?: "N/A", Icons.TwoTone.ColorLens)
                InfoChip(text = court?.located ?: "N/A", Icons.TwoTone.LocationOn)
                InfoChip(text = court?.price.toString(), Icons.TwoTone.EuroSymbol)
            }


            // Selector de horarios
            BookingFilterChip(
                //timeOffer = timeOffer,
                onHourSelected = { onHourSelected(it) },
                freeHoursList = freeHoursList,
                selectedHour = selectedHour
            )

            // Botón de reservar
            CourtButton(
                isLoading = isLoading,
                text = "Book Now for ${court?.price}€",
                isEnabled = selectedHour != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f)
                    .padding(16.dp),
                shape = ShapeDefaults.Small,
                onClick = { onBookClicked() }
            )
        }
    }
}


@Composable
fun InfoChip(
    text: String,
    imageVector: ImageVector,
    modifier: Modifier = Modifier
) {

    Surface(
        shape = RoundedCornerShape(12.dp), // más pill/etiqueta
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ), // borde tenue
        color = MaterialTheme.colorScheme.surface, // fondo plano, no variant
        tonalElevation = 0.dp,
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 4.dp),
                imageVector = imageVector,
                contentDescription = "Inicio"
            )

            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 6.dp, end = 10.dp, top = 4.dp, bottom = 4.dp)
            )
        }
    }
}