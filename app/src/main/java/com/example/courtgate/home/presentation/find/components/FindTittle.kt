package com.example.courtgate.home.presentation.find.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.courtgate.core.presentation.CourtTittle
import com.example.courtgate.ui.theme.CourtGateTheme

@Composable
fun FindTittle(){
    CourtTittle(
        text = "Find a Court",
        style = MaterialTheme.typography.titleLarge
    )
}