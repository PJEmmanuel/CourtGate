package com.example.courtgate.ui.presentation.find.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.courtgate.ui.presentation.core.CourtTittle

//TODO revisar la necesidad de esto
@Composable
fun FindTittle(){
    CourtTittle(
        text = "Find a Court",
        style = MaterialTheme.typography.titleLarge
    )
}