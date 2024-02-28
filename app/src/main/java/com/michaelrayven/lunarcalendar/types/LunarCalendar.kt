package com.michaelrayven.lunarcalendar.types

import com.michaelrayven.lunarcalendar.util.getMonthByNumber
import kotlinx.serialization.Serializable

@Serializable
data class LunarCalendar(
    val currentDay: LunarDay,
    val calendar: List<DayData>
) {
    @Serializable
    data class DayData(
        val day: Int,
        val month: Int,
        val year: Int,
        val timeTable: List<TimeData>
    ) {
        val date: String get() = "$day ${getMonthByNumber(month)}, $year г."
    }

    @Serializable
    data class TimeData(
        val time: String,
        val data: String
    )
}
