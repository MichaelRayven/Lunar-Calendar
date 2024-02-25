package com.michaelrayven.lunarcalendar.remote

import com.michaelrayven.lunarcalendar.types.LunarDay
import com.michaelrayven.lunarcalendar.types.TimeData
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import it.skrape.core.htmlDocument
import it.skrape.selects.html5.tbody
import it.skrape.selects.html5.td
import it.skrape.selects.html5.tr
import java.util.Calendar

class AppClient {

    suspend fun fetchData(timeZone: String): List<LunarDay> {
        val client = HttpClient(Android) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }

        val now = Calendar.getInstance()

        val params = Parameters.build {
            append("fd", "${now.get(Calendar.DAY_OF_MONTH)}")
            append("fm", "${now.get(Calendar.MONTH) + 1}")
            append("fy", "${now.get(Calendar.YEAR)}")
            append("fh", "12")
            append("fmn", "0")
            append("ttz", "20")
            append("tz", timeZone)
            append("sb", "1")
        }

        val response = client.submitForm(BASE_URL, params, encodeInQuery = true)
        val body = response.bodyAsText()

        client.close()

        return parse(body)
    }

    private fun parse(html: String): List<LunarDay> {
        return htmlDocument(html) {
            val rows = "div.CSSTableGenerator > table > tbody  > tr" {
                findAll { this }
            }

            val dayData = rows
                .drop(1)
                .map { row ->
                    val date = row.td {
                        findFirst { text }
                    }.trim().split(" ")

                    val day = date[0].toInt()
                    val month = date[1]
                    val year = date[2].toInt()
                    val dayOfWeek = date[3]

                    val timeRows = row.tbody {
                        tr {
                            findAll { this }
                        }
                    }

                    val timeData = timeRows.map {
                        val time = it.td {
                            findFirst { text }
                        }

                        val info = it.td {
                            findSecond { text }
                        }

                        TimeData(time, info)
                    }

                    LunarDay(
                        day = day,
                        month = month,
                        year = year,
                        dayOfWeek = dayOfWeek,
                        timeData = timeData
                    )
                }

            dayData
        }
    }

    companion object {
        const val BASE_URL = "https://geocult.ru/lunnyiy-kalendar-2"
    }
}