package com.example.courtgate.home.presentation.core

sealed class NavigationBarOnClick {
    object GoToHome : NavigationBarOnClick()
    object GoToBooking : NavigationBarOnClick()
    object GoToMatch : NavigationBarOnClick()
    object GoToSetting : NavigationBarOnClick()
}