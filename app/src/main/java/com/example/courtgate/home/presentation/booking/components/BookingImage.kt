package com.example.courtgate.home.presentation.booking.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.courtgate.R
import com.example.courtgate.navigation.screens.Booking

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