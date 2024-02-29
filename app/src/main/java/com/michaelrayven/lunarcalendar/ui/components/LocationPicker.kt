package com.michaelrayven.lunarcalendar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelrayven.lunarcalendar.remote.AppClient
import com.michaelrayven.lunarcalendar.types.City
import com.michaelrayven.lunarcalendar.types.Country
import com.michaelrayven.lunarcalendar.types.Location
import com.michaelrayven.lunarcalendar.types.State
import kotlinx.coroutines.launch

class LocationPickerState(
    default: Location? = null
) {
    var value by mutableStateOf(default)
    var country by mutableStateOf<Country?>(null)
    var city by mutableStateOf<City?>(null)
    var state by mutableStateOf<State?>(null)
}

@Composable
fun rememberLocationPickerState(default: Location? = null) = remember { LocationPickerState(default) }

@Composable
fun LocationPicker(state: LocationPickerState) {
    val client = AppClient()

    var statesList by rememberSaveable { mutableStateOf(emptyList<State>()) }
    var citiesList by rememberSaveable { mutableStateOf(emptyList<City>()) }

    var selectedCountryIndex by remember { mutableIntStateOf(-1) }
    var selectedStateIndex by remember { mutableIntStateOf(-1) }
    var selectedCityIndex by remember { mutableIntStateOf(-1) }

    fun updateState(countryIndex: Int = -1, stateIndex: Int = -1, cityIndex: Int = -1, value: Location? = null) {
        selectedCountryIndex = countryIndex
        selectedStateIndex = stateIndex
        selectedCityIndex = cityIndex
        state.country = Country.COUNTRY_LIST.getOrNull(countryIndex)
        state.state = statesList.getOrNull(stateIndex)
        state.city = citiesList.getOrNull(cityIndex)
        state.value = value
    }

    // Dropdown actions
    LaunchedEffect(state.country) {
        launch {
            val fetchedStates = state.country?.let { client.getStates(it) }
            fetchedStates?.let {
                statesList = it
            }
        }
    }

    LaunchedEffect(state.state) {
        launch {
            val fetchedCities = state.state?.let { client.getCities(it) }
            fetchedCities?.let { citiesList = it }
        }
    }

    LaunchedEffect(state.city) {
        launch {
            if (state.city != null && state.state != null && state.country != null) {
                val fetchedLocation = client.getLocation(
                    state.city!!,
                    state.state!!,
                    state.country!!
                )
                fetchedLocation?.let { state.value = it}
            }
        }
    }

    Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        DialogDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            label = "Выберете страну...",
            notSetLabel = "Выберете страну...",
            items = Country.COUNTRY_LIST.map { it.name },
            selectedIndex = selectedCountryIndex,
            onItemSelected = { index, _ ->
                if (index != selectedCountryIndex) {
                    updateState(index)
                }
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        DialogDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            label = "Выберете регион...",
            notSetLabel = "Выберете регион...",
            items = statesList.map { it.name },
            selectedIndex = selectedStateIndex,
            onItemSelected = { index, _ ->
                if (index != selectedStateIndex) {
                    updateState(selectedCountryIndex, index)
                }
            },
            enabled = selectedCountryIndex != -1 && statesList.isNotEmpty()
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        DialogDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            label = "Выберете город...",
            notSetLabel = "Выберете город...",
            items = citiesList.map { it.name },
            selectedIndex = selectedCityIndex,
            onItemSelected = { index, _ ->
                if (index != selectedCityIndex) {
                    updateState(selectedCountryIndex, selectedStateIndex, index)
                }
            },
            enabled = selectedStateIndex != -1 && citiesList.isNotEmpty()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 12.dp, top = 16.dp, bottom = 24.dp),
            text = "Select location",
            style = MaterialTheme.typography.labelLarge
        )
        content()
    }
}