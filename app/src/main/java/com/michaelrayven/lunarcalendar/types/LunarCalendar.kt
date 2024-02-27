package com.michaelrayven.lunarcalendar.types

data class LunarCalendar(
    val day: LunarDay,
    val calendar: List<DayData>
) {
    data class DayData(
        val day: Int,
        val month: Int,
        val year: Int,
        val timeTable: List<TimeData>
    )

    data class TimeData(
        val time: String,
        val data: String
    )
}
