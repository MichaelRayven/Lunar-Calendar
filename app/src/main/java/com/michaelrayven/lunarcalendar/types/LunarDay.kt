package com.michaelrayven.lunarcalendar.types

import kotlinx.serialization.Serializable

@Serializable
data class LunarDay(
    val day: Int,
    val month: Int,
    val year: Int,
    val hour: Int,
    val minute: Int,
    val imageUrl: String,
    val lunarDay: Int,
    val currentLunarPhase: String,
    val sign: Sign?,
    val period: String,
    val sunrise: String,
    val sunset: String,
    val visibility: String,
    val distance: String,
    val nextPhases: List<Pair<String, String>>,
    val location: Location
)