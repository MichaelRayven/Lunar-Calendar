package com.michaelrayven.lunarcalendar.remote

import android.util.Log
import androidx.compose.ui.text.toLowerCase
import com.michaelrayven.lunarcalendar.types.City
import com.michaelrayven.lunarcalendar.types.Country
import com.michaelrayven.lunarcalendar.types.Location
import com.michaelrayven.lunarcalendar.types.LunarCalendar
import com.michaelrayven.lunarcalendar.types.LunarDay
import com.michaelrayven.lunarcalendar.types.Sign
import com.michaelrayven.lunarcalendar.types.State
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import io.ktor.serialization.kotlinx.json.json
import it.skrape.core.htmlDocument
import it.skrape.matchers.toBe
import it.skrape.matchers.toBeNot
import it.skrape.selects.attribute
import it.skrape.selects.html5.a
import it.skrape.selects.html5.option
import it.skrape.selects.html5.span
import it.skrape.selects.html5.tbody
import it.skrape.selects.html5.td
import it.skrape.selects.html5.tr
import it.skrape.selects.text
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale


class AppClient {
    suspend fun getStates(country: Country): List<State>? {
        return try {
            val client = HttpClient(Android) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                }
            }

            val params = Parameters.build {
                append("country_id", country.code)
            }

            val response = client.submitForm(DATA_URL, params)

            client.close()

            val states = htmlDocument(response.bodyAsText()) {
                option {
                    findAll {
                        mapNotNull {
                            val code = it.attribute("value")
                            val name = it.text
                            if (code.isEmpty()) null else State(name, code)
                        }
                    }
                }.filter { it.code.split(".").size == 2 }
            }

            states
        } catch (e: Exception) {
            Log.e("DATA_EXTRACTION", e.message ?: "")

            null
        }
    }

    suspend fun getCities(state: State): List<City>? {
        return try {
            val client = HttpClient(Android) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                }
            }

            val params = Parameters.build {
                append("state_id", state.code)
            }

            val response = client.submitForm(DATA_URL, params)

            client.close()

            val cities = htmlDocument(response.bodyAsText()) {
                option {
                    findAll {
                        mapNotNull {
                            val code = it.attribute("value")
                            val name = it.text
                            if (code.isEmpty()) null else City(name, code)
                        }
                    }
                }
            }

            cities
        } catch (e: Exception) {
            Log.e("DATA_EXTRACTION", e.message ?: "")

            null
        }
    }

    suspend fun getLocation(city: City, state: State, country: Country): Location? {
        return try {
            val client = HttpClient(Android) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                }
            }

            val params = Parameters.build {
                append("city_id", city.code)
            }

            val body = client.submitForm(DATA_URL, params).bodyAsText()
            val response = Json.decodeFromString<APIResponseLocation>(body)

            client.close()

            Location(
                latitude = response.latitude.toFloat(),
                longitude = response.longitude.toFloat(),
                gmt = response.gmt.toInt(),
                timeZone = response.timezone,
                country = country,
                state = state,
                city = city
            )
        } catch (e: Exception) {
            Log.e("DATA_EXTRACTION", e.message ?: "")

            null
        }
    }

    private suspend fun fetchCalendarHtml(
        location: Location,
        day: Int,
        month: Int,
        year: Int,
        hour: Int = 12,
        minute: Int = 0
    ): String {
        val client = HttpClient(Android) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }

        if (month < 1 || month > 12) {
            throw Exception("Invalid month: out of range")
        }

        val calendar = GregorianCalendar(year, month - 1, 1)

        if (day < 1 || day > calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            throw Exception("Invalid day: out of range")
        }

        val params = Parameters.build {
            append("fd", day.toString())
            append("fm", month.toString())
            append("fy", year.toString())
            append("fh", hour.toString())
            append("fmn", minute.toString())
            append("ttz", "20")
            append("c1", location.displayName)
            append("tz", location.timeZone)
            append("tm", location.gmt.toString())
            append("lt", location.latitude.toString())
            append("ln", location.longitude.toString())
            append("sb", "1")
        }

        val response = client.submitForm(BASE_URL, params, encodeInQuery = true).bodyAsText()

        client.close()

        return response
    }

    suspend fun getLunarDay(
        location: Location,
        day: Int,
        month: Int,
        year: Int,
        hour: Int = 12,
        minute: Int = 0
    ): LunarDay? {
        return try {
            val html = fetchCalendarHtml(
                location, day, month, year, hour, minute
            )

            getLunarDay(html, location, day, month, year, hour, minute)
        } catch (e: Exception) {
            Log.e("DATA_EXTRACTION", e.message ?: "")

            null
        }
    }

    private fun getLunarDay(
        html: String,
        location: Location,
        day: Int,
        month: Int,
        year: Int,
        hour: Int = 12,
        minute: Int = 0
    ): LunarDay {
        return htmlDocument(html) {
            val imageUrl = ".imgmoonmedia > img" {
                findFirst { attribute("src") }
            }
            val lunarDayNumber = ".moonzagolovok" {
                findFirst { text.trim() }
            }
            val lunarPhase = td(".tableform2") {
                findByIndex(0) {
                    text.trim().split("~").joinToString("\n~")
                }
            }
            val sign = td(".tableform2") {
                findByIndex(1) {
                    a {
                        findFirst {
                            val signCharCode = span {
                                findFirst {
                                    className toBe "hamburg"
                                    text
                                }
                            }
                            val sign = Sign.getByCharCode(signCharCode)
                            sign?.extra = text.split(" ").drop(1).joinToString(" ")
                            sign
                        }
                    }
                }
            }
            val period = td(".tableform2") {
                findByIndex(2) {
                    text.trim()
                }
            }
            val sunrise = td(".tableform2") {
                findByIndex(3) {
                    text.trim()
                }
            }
            val sunset = td(".tableform2") {
                findByIndex(4) {
                    text.trim()
                }
            }
            val visibility = td(".tableform2") {
                findByIndex(5) {
                    text.trim()
                }
            }
            val distance = td(".tableform2") {
                findByIndex(6) {
                    text.trim()
                }
            }
            val phaseNames = td(".tableform1") {
                listOf(
                    findByIndex(8) {
                        text.trim()
                    },
                    findByIndex(9) {
                        text.trim()
                    },
                    findByIndex(10) {
                        text.trim()
                    },
                    findByIndex(11) {
                        text.trim()
                    }
                )
            }
            val nextPhases = phaseNames.mapIndexed { index, name ->
                val date = td(".tableform2") {
                    findByIndex(8 + index) {
                        text.trim()
                    }
                }

                Pair(name, date)
            }

            LunarDay(
                day = day,
                month = month,
                year = year,
                hour = hour,
                minute = minute,
                imageUrl = imageUrl,
                lunarDay = lunarDayNumber.toInt(),
                currentLunarPhase = lunarPhase,
                sign = sign,
                period = period,
                sunrise = sunrise,
                sunset = sunset,
                visibility = visibility,
                distance = distance,
                nextPhases = nextPhases,
                location = location
            )
        }
    }

    suspend fun getLunarCalendar(
        location: Location,
        day: Int,
        month: Int,
        year: Int,
        hour: Int = 12,
        minute: Int = 0
    ): LunarCalendar? {
        return try {
            val html = fetchCalendarHtml(location, day, month, year, hour, minute)
            val lunarDay = getLunarDay(html, location, day, month, year, hour, minute)
            val dayData = parseDayData(html)

            LunarCalendar(
                day = lunarDay,
                calendar = dayData
            )
        } catch (e: Exception) {
            Log.e("DATA_EXTRACTION", e.message ?: "")

            null
        }
    }

    private fun parseDayData(
        html: String
    ): List<LunarCalendar.DayData> {
        return htmlDocument(html) {
                val dayRows = "div.CSSTableGenerator > table > tbody > tr" {
                    findAll { this }
                }.drop(1)

                val dayData = dayRows.map { dayRow ->
                    val (rowDay, rowMonthName, rowYear, _) = dayRow.td {
                        findFirst {
                            text
                        }
                    }.split(" ")

                    val timeRows = dayRow.tr {
                        findAll { this }
                    }

                    val timeTable = timeRows.map { timeRow ->
                        val time = timeRow.td {
                            findFirst {
                                text
                            }
                        }
                        val data = timeRow.td {
                            findSecond {
                                text
                            }
                        }

                        LunarCalendar.TimeData(time, data)
                    }

                    LunarCalendar.DayData(
                        day = rowDay.toInt(),
                        month = getMonthByName(rowMonthName),
                        year = rowYear.toInt(),
                        timeTable = timeTable
                    )
                }

                dayData
            }
    }

    private fun getMonthByName(
        name: String
    ): Int {
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

    @Serializable
    private data class APIResponseLocation(
        val latitude: String,
        val longitude: String,
        val gmt: String,
        val timezone: String,
        val countryid: String,
        val stateid: String,
        val geonameid: String
    )

    companion object {
        const val BASE_URL = "https://geocult.ru/lunnyiy-kalendar-2"
        const val DATA_URL = "https://geocult.ru/scripts/app_moon/form/fetch/fetch_data.php"
    }
}

