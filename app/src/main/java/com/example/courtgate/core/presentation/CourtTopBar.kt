package com.example.courtgate.core.presentation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourtTopBar(
    navIcon : @Composable () -> Unit,
    modifier: Modifier = Modifier
){
    TopAppBar(
        title = { Text("prueba") },
        modifier = modifier,
        navigationIcon = { navIcon() },
        colors = TODO(), //TODO: comprobar
    )
}