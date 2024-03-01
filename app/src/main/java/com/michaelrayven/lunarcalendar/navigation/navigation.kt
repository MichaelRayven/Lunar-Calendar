package com.michaelrayven.lunarcalendar.navigation

import android.content.Context
import android.os.Bundle
import com.michaelrayven.lunarcalendar.R
import com.michaelrayven.lunarcalendar.types.Location
import com.michaelrayven.lunarcalendar.util.getSavedLocation
import kotlinx.serialization.json.Json
import java.time.Instant
import java.util.Base64

enum class Screens {
    HOME,
    SETTINGS,
    CALENDAR;
}

sealed class NavigationItem(val name: String, val route: String, val icon: Int, args: List<String> = emptyList()) {
    data object Home : NavigationItem("Главная", Screens.HOME.name, R.drawable.baseline_home_filled_24)
    data object Settings : NavigationItem("Настройки", Screens.SETTINGS.name, R.drawable.baseline_settings_24)
    data object Calendar : NavigationItem("Календарь", Screens.CALENDAR.name, R.drawable.baseline_calendar_month_24, listOf("location", "timestamp")) {
        fun processDestinationArgs(context: Context, args: Bundle?): DestinationArgs {
            var locationString = args?.getString("location") ?: ""
            val timestampString = args?.getString("timestamp") ?: ""

            val timestamp = timestampString.toLongOrNull() ?: Instant.now().toEpochMilli()
            locationString = String(Base64.getUrlDecoder().decode(locationString))
            val location = try {
                Json.decodeFromString<Location>(locationString)
            } catch (e: IllegalArgumentException) {
                getSavedLocation(context)
            }

            return DestinationArgs(location, timestamp)
        }

        data class DestinationArgs(
            val location: Location,
            val timestamp: Long
        )
    }

    val routeWithArgs = args.fold(route) { acc, s -> "$acc/{$s}" }
}