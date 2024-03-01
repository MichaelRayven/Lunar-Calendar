package com.michaelrayven.lunarcalendar.util

import android.content.Context
import com.michaelrayven.lunarcalendar.R
import com.michaelrayven.lunarcalendar.remote.AppClient
import com.michaelrayven.lunarcalendar.types.Location
import com.michaelrayven.lunarcalendar.types.LunarCalendar
import com.michaelrayven.lunarcalendar.types.Sign
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.internal.toLongOrDefault
import java.text.DecimalFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

fun getSavedLocation(context: Context): Location {
    val preferencesFile = context.getString(R.string.preference_file)
    val preferences = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)

    return try {
        Json.decodeFromString<Location>(
            preferences.getString(context.getString(R.string.saved_location), null) ?: ""
        )
    } catch (e: IllegalArgumentException) {
        Location.DEFAULT
    }
}

fun saveLocation(context: Context, location: Location) {
    val preferencesFile = context.getString(R.string.preference_file)
    val preferences = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)

    with(preferences.edit()) {
        putString(
            context.getString(R.string.saved_location),
            Json.encodeToString(location)
        )
        apply()
    }
}

fun getIntervalFromString(hoursString: String, minutesString: String): Long {
    val hours = hoursString.toLongOrDefault(0)
    val minutes = minutesString.toLongOrDefault(0)
    var interval = ((hours * 60 + minutes) * 60 * 1000)

    if (interval == 0L) {
        interval = 24 * 60 * 60 * 1000
    }

    return interval
}

fun saveUpdateInterval(context: Context, hoursString: String, minutesString: String) {
    val preferencesFile = context.getString(R.string.preference_file)
    val preferences = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)

    with(preferences.edit()) {
        putLong(
            context.getString(R.string.saved_update_interval),
            getIntervalFromString(hoursString, minutesString)
        )
        apply()
    }
}

fun getSavedUpdateInterval(context: Context): Long {
    val preferencesFile = context.getString(R.string.preference_file)
    val preferences = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)

    return preferences.getLong(
        context.getString(R.string.saved_update_interval),
        24 * 60 * 60 * 1000
    )
}

fun formatGmt(gmt: Float): String {
    val format = DecimalFormat("0.#")

    return if (gmt > 0)
        "GMT +${format.format(gmt)}"
    else
        "GMT ${format.format(gmt)}"
}

fun formatInterval(interval: Long): String {
    val hours = (interval / 1000 / 60 / 60) % 24
    val minutes = (interval / 1000 / 60) % 60
    return if (hours == 0L && minutes == 0L) {
        "раз в день"
    } else {
        val minuteString = if (minutes > 0) "$minutes минут" else ""
        val hourString = if (hours > 0) "$hours часов" else ""
        "каждые ${listOf(hourString, minuteString).joinToString(" ").trim()}"
    }
}

suspend fun getCurrentLunarCalendar(location: Location, timestamp: Long? = null): LunarCalendar? {
    val client = AppClient()
    val calendar = Calendar.getInstance()

    if (timestamp != null) {
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar.timeInMillis = timestamp
    } else {
        calendar.timeZone = TimeZone.getTimeZone(location.timeZone)
    }

    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH) + 1
    val year = calendar.get(Calendar.YEAR)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    return client.getLunarCalendar(location, day, month, year, hour, minute)
}

fun cacheLunarCalendar(context: Context, lunarCalendar: LunarCalendar) {
    val preferencesFile = context.getString(R.string.preference_file)
    val preferences = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)
    val key = context.getString(R.string.saved_calendar)

    with (preferences.edit()) {
        putString(key, Json.encodeToString(lunarCalendar))
        apply()
    }
}

fun getCachedLunarCalendar(context: Context): LunarCalendar? {
    val preferencesFile = context.getString(R.string.preference_file)
    val preferences = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)
    val key = context.getString(R.string.saved_calendar)

    return try {
        Json.decodeFromString<LunarCalendar>(preferences.getString(key, null) ?: "")
    } catch (e: IllegalArgumentException) {
        return null
    }
}

fun getMonthByName(name: String): Int {
    return when(name.lowercase()) {
        "января" -> 1
        "февраля" -> 2
        "марта" -> 3
        "апреля" -> 4
        "мая" -> 5
        "июня" -> 6
        "июля" -> 7
        "августа" -> 8
        "сентября" -> 9
        "октября" -> 10
        "ноября" -> 11
        "декабря" -> 12
        else -> throw Exception("Invalid month name")
    }
}

fun getMonthByNumber(number: Int): String {
    return when(number) {
        1 -> "Января"
        2 -> "Февраля"
        3 -> "Марта"
        4 -> "Апреля"
        5 -> "Мая"
        6 -> "Июня"
        7 -> "Июля"
        8 -> "Августа"
        9 -> "Сентября"
        10 -> "Октября"
        11 -> "Ноября"
        12 -> "Декабря"
        else -> throw Exception("Invalid month number")
    }
}

fun extractSign(text: String): Pair<String, Sign?> {
    val signRegex = "[asdfghjklzxc]".toRegex(setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))
    val signMatch = signRegex.find(text)
    val sign = signMatch?.let { Sign.getByCharCode(it.value) }
    val newText = if (sign != null) {
        text.replace(sign.charCode + " ", "")
    } else {
        text
    }.replaceFirstChar { it.titlecase(Locale.ROOT) }

    return Pair(newText, sign)
}
