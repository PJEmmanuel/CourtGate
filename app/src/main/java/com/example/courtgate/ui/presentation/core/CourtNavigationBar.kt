package com.example.courtgate.ui.presentation.core

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
import androidx.compose.ui.res.stringResource
import com.example.courtgate.R

@Composable
fun CourtNavigationBar(
    onNavigate: (NavigationBarOnClick) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = false,
            onClick = { onNavigate(NavigationBarOnClick.GoToHome) },
            icon = {
                Icon(
                    imageVector = Icons.TwoTone.Home,
                    contentDescription = stringResource(R.string.nav_home)
                )
            },
            label = { Text(stringResource(R.string.nav_home)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { onNavigate(NavigationBarOnClick.GoToBooking) },
            icon = {
                Icon(
                    imageVector = Icons.TwoTone.EventAvailable,
                    contentDescription = stringResource(R.string.nav_booking)
                )
            },
            label = { Text(stringResource(R.string.nav_booking)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { onNavigate(NavigationBarOnClick.GoToMatch) },
            icon = {
                Icon(
                    imageVector = Icons.TwoTone.Book,
                    contentDescription = stringResource(R.string.nav_match)
                )
            },
            label = { Text(stringResource(R.string.nav_match)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { onNavigate(NavigationBarOnClick.GoToSetting) },
            icon = {
                Icon(
                    imageVector = Icons.TwoTone.Person,
                    contentDescription = stringResource(R.string.nav_settings)
                )
            },
            label = { Text(stringResource(R.string.nav_settings)) }
        )
    }
}
