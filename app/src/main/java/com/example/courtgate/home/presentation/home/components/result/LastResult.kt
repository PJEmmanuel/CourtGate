package com.example.courtgate.home.presentation.home.components.result

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LastResult(
    firstResult: Int,
    secondResult: Int,
    modifier: Modifier = Modifier
) {
    Box (
        modifier = modifier.fillMaxSize()
    ) {
        Row {
            LastResultForm(
                firstResult = firstResult,
                secondResult = secondResult,
                modifier = Modifier
            )
            //TODO : Como sabemos cuantos set mostrar? o mostrar 3 y por defecto vacios con ceros
        }
    }

}