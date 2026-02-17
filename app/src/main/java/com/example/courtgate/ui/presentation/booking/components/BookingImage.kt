package com.example.courtgate.ui.presentation.booking.components

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
        "default image",
        modifier = modifier, // 👈 importante
        contentScale = ContentScale.Crop // Para que no se deforme

    )
}