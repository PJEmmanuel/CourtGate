package com.example.courtgate.home.presentation.core

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

//TODO Poner en VALUE
//TODO Poner en VALUE
//TODO Poner en VALUE
//TODO Poner en VALUE
@Composable
fun CourtNavigationBar(
    onNavigate: (NavigationBarOnClick) -> Unit,
    modifier: Modifier = Modifier
) {

    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = true,
            onClick = { onNavigate(NavigationBarOnClick.GoToHome) },
            icon = { Icon(imageVector = Icons.TwoTone.Home, contentDescription = "Home") },
            label = { Text("Home") } //TODO Poner en VALUE
        )
        NavigationBarItem(
            selected = true,
            onClick = { onNavigate(NavigationBarOnClick.GoToBooking) },
            icon = {
                Icon(
                    imageVector = Icons.TwoTone.EventAvailable,
                    contentDescription = "Booking"
                )
            },
            label = { Text("Booking") }//TODO Poner en VALUE

        )
        NavigationBarItem(
            selected = true,
            onClick = { onNavigate(NavigationBarOnClick.GoToMatch) },
            icon = { Icon(imageVector = Icons.TwoTone.Book, contentDescription = "Match") },
            label = { Text("Match") }//TODO Poner en VALUE

        )
        NavigationBarItem(
            selected = true,
            onClick = { onNavigate(NavigationBarOnClick.GoToSetting) },
            icon = { Icon(imageVector = Icons.TwoTone.Person, contentDescription = "Setting") },
            label = { Text("Setting") }//TODO Poner en VALUE

        )
    }
}