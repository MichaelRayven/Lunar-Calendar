package com.michaelrayven.lunarcalendar.types

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val name: String,
    val code: String
)
