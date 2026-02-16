package com.example.courtgate.core.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.courtgate.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourtTopBar(
    navIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = {},
        modifier = modifier,
        navigationIcon = { navIcon() },
        actions = {
            Image(
                painter = painterResource(R.drawable.avatar),
                contentDescription = "Avatar",
                modifier = Modifier.clip(shape = CircleShape)
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent, // 👈 fondo transparente
            scrolledContainerColor = Color.Transparent
        )
    )
}