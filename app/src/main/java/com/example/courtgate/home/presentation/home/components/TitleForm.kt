package com.example.courtgate.home.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.courtgate.core.presentation.CourtTittle

@Composable
fun TitleForm(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        CourtTittle(
            text = "Welcome Back To",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.padding(8.dp))
        CourtTittle(
            text = "Padel Sport Club",
            style = MaterialTheme.typography.titleMedium
        )
    }
}