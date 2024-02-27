package com.michaelrayven.lunarcalendar.types

import kotlinx.serialization.Serializable

@Serializable
data class State(
    val name: String,
    val code: String
) {
    val id: Int get() = code.split(".")[1].toInt()
}
