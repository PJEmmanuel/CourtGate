package com.example.courtgate.home.presentation.home.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Book
import androidx.compose.material.icons.twotone.EventAvailable
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CourtNavigationBar(modifier: Modifier = Modifier) {

    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(imageVector = Icons.TwoTone.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(imageVector = Icons.TwoTone.EventAvailable, contentDescription = "Booking") },
            label = { Text("Booking") }

        )
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(imageVector = Icons.TwoTone.Book, contentDescription = "Match") },
            label = { Text("Match") }

        )
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(imageVector = Icons.TwoTone.Person, contentDescription = "Setting") },
            label = { Text("Setting") }

        )
    }
}