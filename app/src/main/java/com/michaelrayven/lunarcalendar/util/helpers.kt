package com.michaelrayven.lunarcalendar.util

import android.content.Context
import com.michaelrayven.lunarcalendar.R
import com.michaelrayven.lunarcalendar.remote.AppClient
import com.michaelrayven.lunarcalendar.types.Location
import com.michaelrayven.lunarcalendar.types.LunarCalendar
import com.michaelrayven.lunarcalendar.types.Sign
import kotlinx.serialization.json.Json
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

fun parseLocationOrSaved(context: Context, key: String? = ""): Location {
    val preferencesFile = context.getString(R.string.preference_file)
    val preferences = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)

    return try {
        Json.decodeFromString<Location>(
            preferences.getString(key, null) ?: ""
        )
    } catch (e: IllegalArgumentException) {
        getSavedLocation(context)
    }
}

fun formatGmt(gmt: Float): String {
    val format = DecimalFormat("0.#")

    return if (gmt > 0)
        "GMT +${format.format(gmt)}"
    else
        "GMT ${format.format(gmt)}"
}

fun formatInterval(interval: String): String {
    val (hours, minutes) = interval.split(":").map { it.toInt() }
    return if (hours == 0 && minutes == 0) {
        "раз в день"
    } else {
        val minuteString = if (minutes > 0) "$minutes минут" else ""
        val hourString = if (hours > 0) "$hours часов" else ""
        "каждые ${listOf(hourString, minuteString).joinToString(" ").trim()}"
    }
}

suspend fun getCurrentLunarCalendar(location: Location, timestamp: Long? = null): LunarCalendar? {
    val client = AppClient()
    val calendar = Calendar.getInstance(TimeZone.getTimeZone(location.timeZone))

    timestamp?.let {
        calendar.timeZone = TimeZone.getDefault()
        calendar.timeInMillis = timestamp
    }

    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH) + 1
    val year = calendar.get(Calendar.YEAR)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    return client.getLunarCalendar(location, day, month, year, hour, minute)
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
