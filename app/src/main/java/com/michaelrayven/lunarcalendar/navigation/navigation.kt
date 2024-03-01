package com.michaelrayven.lunarcalendar.navigation

import android.content.Context
import android.os.Bundle
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

sealed class NavigationItem(val route: String) {
    data object Home : NavigationItem(Screens.HOME.name)
    data object Settings : NavigationItem(Screens.SETTINGS.name)
    data object Calendar : NavigationItem(Screens.CALENDAR.name) {
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
}