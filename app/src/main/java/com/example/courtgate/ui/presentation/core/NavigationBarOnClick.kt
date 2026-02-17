package com.example.courtgate.ui.presentation.core

sealed class NavigationBarOnClick {
    object GoToHome : NavigationBarOnClick()
    object GoToBooking : NavigationBarOnClick()
    object GoToMatch : NavigationBarOnClick()
    object GoToSetting : NavigationBarOnClick()
}