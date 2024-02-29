package com.michaelrayven.lunarcalendar.navigation

enum class Screens {
    HOME,
    SETTINGS,
    CALENDAR;
}

sealed class NavigationItem(val route: String) {
    object Home : NavigationItem(Screens.HOME.name)
    object Settings : NavigationItem(Screens.SETTINGS.name)
    object Calendar : NavigationItem(Screens.CALENDAR.name)
}