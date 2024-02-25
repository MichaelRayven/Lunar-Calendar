package com.michaelrayven.lunarcalendar.types

data class LunarDay(
    val day: Int,
    val month: String,
    val year: Int,
    val dayOfWeek: String,
    val timeData: List<TimeData>
)