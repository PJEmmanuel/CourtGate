package com.example.courtgate.ui.presentation.find.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.courtgate.R
import com.example.courtgate.ui.presentation.core.CourtTittle

@Composable
fun FindTittle(){
    CourtTittle(
        text = stringResource(R.string.find_court_title),
        style = MaterialTheme.typography.titleLarge
    )
}
