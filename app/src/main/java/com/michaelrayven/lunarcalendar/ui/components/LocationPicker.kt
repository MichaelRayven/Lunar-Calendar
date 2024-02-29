package com.michaelrayven.lunarcalendar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.michaelrayven.lunarcalendar.remote.AppClient
import com.michaelrayven.lunarcalendar.types.City
import com.michaelrayven.lunarcalendar.types.Country
import com.michaelrayven.lunarcalendar.types.Location
import com.michaelrayven.lunarcalendar.types.State
import com.michaelrayven.lunarcalendar.util.getSavedLocation
import kotlinx.coroutines.launch

class LocationPickerState(
    default: Location
) {
    var value by mutableStateOf(default)
}

@Composable
fun rememberLocationPickerState(default: Location) = remember { LocationPickerState(default) }

@Composable
fun LocationPicker(
    confirmButton: @Composable (shouldBeEnabled: Boolean) -> Unit,
    state: LocationPickerState
) {
    val client = AppClient()

    var statesList by rememberSaveable { mutableStateOf(emptyList<State>()) }
    var citiesList by rememberSaveable { mutableStateOf(emptyList<City>()) }

    var selectedCountry by remember { mutableStateOf<Country?>(null) }
    var selectedState by remember { mutableStateOf<State?>(null) }
    var selectedCity by remember { mutableStateOf<City?>(null) }

    var selectedCountryIndex by remember { mutableIntStateOf(-1) }
    var selectedStateIndex by remember { mutableIntStateOf(-1) }
    var selectedCityIndex by remember { mutableIntStateOf(-1) }

    // Dropdown actions
    LaunchedEffect(key1 = selectedCountry) {
        launch {
            val fetchedStates = selectedCountry?.let { client.getStates(it) }
            fetchedStates?.let {
                statesList = it
            }
        }
    }

    LaunchedEffect(key1 = selectedState) {
        launch {
            val fetchedCities = selectedState?.let { client.getCities(it) }
            fetchedCities?.let { citiesList = it }
        }
    }

    LaunchedEffect(key1 = selectedCity) {
        launch {
            if (selectedCity != null && selectedState != null && selectedCountry != null) {
                val fetchedLocation = client.getLocation(
                    selectedCity!!,
                    selectedState!!,
                    selectedCountry!!
                )
                fetchedLocation?.let { state.value = it}
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        DialogDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            label = "Выберете страну...",
            items = Country.COUNTRY_LIST.map { it.name },
            selectedIndex = selectedCountryIndex,
            onItemSelected = { index, _ ->
                if (index != selectedCountryIndex) {
                    selectedCountryIndex = index
                    selectedStateIndex = -1
                    selectedCityIndex = -1
                    selectedCountry = Country.COUNTRY_LIST.getOrNull(index)
                }
            }
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        DialogDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            label = "Выберете регион...",
            items = statesList.map { it.name },
            selectedIndex = selectedStateIndex,
            onItemSelected = { index, _ ->
                if (index != selectedStateIndex) {
                    selectedStateIndex = index
                    selectedCityIndex = -1
                    selectedState = statesList.getOrNull(index)
                }
            },
            enabled = selectedCountry != null && statesList.isNotEmpty()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        DialogDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            label = "Выберете город...",
            items = citiesList.map { it.name },
            selectedIndex = selectedCityIndex,
            onItemSelected = { index, _ ->
                if (index != selectedCityIndex) {
                    selectedCityIndex = index
                    selectedCity = citiesList.getOrNull(index)
                }
            },
            enabled = selectedState != null && citiesList.isNotEmpty()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        confirmButton(selectedCity != null)
    }
}