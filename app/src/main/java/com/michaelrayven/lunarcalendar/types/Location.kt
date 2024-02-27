package com.michaelrayven.lunarcalendar.types

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val latitude: Float,
    val longitude: Float,
    val gmt: Int,
    val timeZone: String,
    val country: Country,
    val state: State,
    val city: City
) {
    val displayName: String get() = "${country.name}, ${city.name}"

    companion object {
        val DEFAULT = Location(
            country = Country("Россия", "RU"),
            state = State("Москва адм. округ", "RU.48"),
            city = City("Москва", "524901"),
            latitude = 55.7522f,
            longitude = 37.6155f,
            gmt = 3,
            timeZone = "Europe/Moscow"
        )
    }
}