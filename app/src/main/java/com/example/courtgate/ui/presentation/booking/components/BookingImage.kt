package com.example.courtgate.ui.presentation.booking.components

import androidx.compose.ui.res.stringResource
import com.example.courtgate.R
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale

@Composable
fun BookingImage(
    imageUrl: Painter,
    modifier: Modifier = Modifier

) {
    Image(
        painter = imageUrl,
        stringResource(R.string.booking_image_description),
        modifier = modifier,
        contentScale = ContentScale.Crop // Para que no se deforme

    )
}