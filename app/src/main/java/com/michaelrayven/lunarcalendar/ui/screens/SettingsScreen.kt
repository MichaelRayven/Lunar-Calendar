package com.michaelrayven.lunarcalendar.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.michaelrayven.lunarcalendar.R
import com.michaelrayven.lunarcalendar.remote.AppClient
import com.michaelrayven.lunarcalendar.types.City
import com.michaelrayven.lunarcalendar.types.Country
import com.michaelrayven.lunarcalendar.types.Location
import com.michaelrayven.lunarcalendar.types.State
import com.michaelrayven.lunarcalendar.ui.components.DialogDropdownMenu
import com.michaelrayven.lunarcalendar.ui.components.LoadingSpinner
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun SettingsScreen(snackbarHostState: SnackbarHostState) {
    val context = LocalContext.current
    val preferencesFile = context.getString(R.string.preference_file)
    val preferences = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)

    val client = AppClient()
    val scope = rememberCoroutineScope()

    // Dropdown variables
    var statesList by remember { mutableStateOf(emptyList<State>()) }
    var citiesList by remember { mutableStateOf(emptyList<City>()) }

    var selectedCountry by remember { mutableStateOf<Country?>(null) }
    var selectedState by remember { mutableStateOf<State?>(null) }
    var selectedCity by remember { mutableStateOf<City?>(null) }
    var selectedLocation by remember { mutableStateOf<Location?>(null) }

    var selectedCountryIndex by remember { mutableIntStateOf(-1) }
    var selectedStateIndex by remember { mutableIntStateOf(-1) }
    var selectedCityIndex by remember { mutableIntStateOf(-1) }

    // Dropdown actions
    LaunchedEffect(key1 = selectedCountry) {
        launch {
            val fetchedStates = selectedCountry?.let { client.getStates(it) }
            fetchedStates?.let { statesList = it }
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
                fetchedLocation?.let { selectedLocation = it }
            }

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            text = "Настройки:"
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall,
            text = "Смена локации"
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        DialogDropdownMenu(
            modifier = Modifier
                .fillMaxWidth()
            ,
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
            modifier = Modifier
                .fillMaxWidth(),
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
            enabled = selectedCountry != null
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        DialogDropdownMenu(
            modifier = Modifier
                .fillMaxWidth(),
            label = "Выберете город...",
            items = citiesList.map { it.name },
            selectedIndex = selectedCityIndex,
            onItemSelected = { index, _ ->
                if (index != selectedCityIndex) {
                    selectedCityIndex = index
                    selectedCity = citiesList.getOrNull(index)
                }
            },
            enabled = selectedState != null
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            onClick = {
                if (selectedLocation != null) {
                    with(preferences.edit()) {
                        putString(context.getString(R.string.saved_location), Json.encodeToString(selectedLocation!!))
                        apply()
                    }
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Пожалуйста выберете одну из доступных локаций.",
                            actionLabel = "ОК",
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            },
            enabled = selectedCity != null
        ) {
            if (selectedCity != null && selectedLocation == null) {
                LoadingSpinner(modifier = Modifier.size(16.dp))
            } else {
                Text(text = "Сохранить")
            }
        }
    }
}