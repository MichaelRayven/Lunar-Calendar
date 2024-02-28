package com.michaelrayven.lunarcalendar.ui.screens

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.work.await
import com.michaelrayven.lunarcalendar.R
import com.michaelrayven.lunarcalendar.remote.AppClient
import com.michaelrayven.lunarcalendar.types.City
import com.michaelrayven.lunarcalendar.types.Country
import com.michaelrayven.lunarcalendar.types.Location
import com.michaelrayven.lunarcalendar.types.State
import com.michaelrayven.lunarcalendar.ui.components.DialogDropdownMenu
import com.michaelrayven.lunarcalendar.ui.components.DialogDropdownMenuItem
import com.michaelrayven.lunarcalendar.ui.components.LoadingSpinner
import com.michaelrayven.lunarcalendar.ui.components.Picker
import com.michaelrayven.lunarcalendar.ui.components.rememberPickerState
import com.michaelrayven.lunarcalendar.util.formatGmt
import com.michaelrayven.lunarcalendar.util.formatInterval
import com.michaelrayven.lunarcalendar.util.getSavedLocation
import com.michaelrayven.lunarcalendar.work.WidgetUpdateWorker
import com.michaelrayven.lunarcalendar.work.WidgetUpdateWorker.Companion.updateWidget
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
    var selectedLocation by remember { mutableStateOf(getSavedLocation(context)) }

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
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            text = "Настройки:",
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

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Текущая локация: ${selectedLocation.displayName} (${formatGmt(selectedLocation.gmt)})",
            color = MaterialTheme.colorScheme.secondary
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
                with(preferences.edit()) {
                    putString(context.getString(R.string.saved_location), Json.encodeToString(
                        selectedLocation
                    ))
                    apply()
                }
            },
            enabled = selectedCity != null
        ) {
            Text(text = "Сохранить")
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
        
        Text(
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall,
            text = "Виджеты"
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        val timeOptions = listOf("Системное", "${selectedLocation.city.name}, (${formatGmt(selectedLocation.gmt)})")
        var selectedTimeIndex by remember { mutableIntStateOf(0) }

        DialogDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            label = "Используемое время...",
            items = timeOptions,
            selectedIndex = selectedTimeIndex,
            onItemSelected = { index, _ ->
                selectedTimeIndex = index
                with(preferences.edit()) {
                    if (index == 0) {
                        putString(context.getString(R.string.saved_timezone), "system")
                    } else {
                        putString(context.getString(R.string.saved_timezone), "local")
                    }
                    apply()
                }
            }
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        var updatingWidgets by remember { mutableStateOf(false) }
        Button(
            modifier = Modifier.wrapContentSize(),
            onClick = {
                scope.launch {
                    updatingWidgets = true
                    updateWidget(context).await()
                    updatingWidgets = false
                }
            }
        ) {
            if (updatingWidgets) {
                LoadingSpinner(modifier = Modifier.size(16.dp))
            } else {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "Обновить виджеты"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        IntervalPicker(preferences, context)
    }
}

@Composable
private fun IntervalPicker(
    preferences: SharedPreferences,
    context: Context
) {
    val interval = preferences.getString(context.getString(R.string.saved_update_interval), null) ?: "00:00"
    var expanded by remember { mutableStateOf(false) }


    Text(
        text = "Частота обновления виджетов:",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    )

    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "Текущий интервал: ${formatInterval(interval)}\nМинимум раз в 15 минут.",
        color = MaterialTheme.colorScheme.secondary
    )

    Spacer(
        modifier = Modifier.height(16.dp)
    )

    Button(
        modifier = Modifier.wrapContentSize(),
        onClick = {
            expanded = true
        }
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Изменить интервал"
        )
    }

    if (expanded) {
        Dialog(
            onDismissRequest = { expanded = false },
        ) {
            MaterialTheme {
                Surface(
                    shadowElevation = 16.dp,
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        val hourValues = (0..23).map { it.toString().padStart(2, '0') }
                        val hourPickerState = rememberPickerState("00")

                        val minuteValues = (0..59).map { it.toString().padStart(2, '0') }
                        val minutePickerState = rememberPickerState("00")

                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Текущий интервал: ${formatInterval(interval)}",
                            style = MaterialTheme.typography.headlineSmall,
                        )

                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Новый интервал: ${formatInterval("${hourPickerState.selectedItem}:${minutePickerState.selectedItem}")}",
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.weight(0.7f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Часы"
                                )
                                Picker(
                                    state = hourPickerState,
                                    items = hourValues,
                                    modifier = Modifier.fillMaxWidth(),
                                    textModifier = Modifier.padding(8.dp),
                                    textStyle = TextStyle(fontSize = 16.sp)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(0.7f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Минуты"
                                )
                                Picker(
                                    state = minutePickerState,
                                    items = minuteValues,
                                    modifier = Modifier.fillMaxWidth(),
                                    textModifier = Modifier.padding(8.dp),
                                    textStyle = TextStyle(fontSize = 16.sp)
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp),
                            onClick = {
                                with(preferences.edit()) {
                                    putString(context.getString(R.string.saved_update_interval), "${hourPickerState.selectedItem}:${minutePickerState.selectedItem}")
                                    apply()
                                }
                                WidgetUpdateWorker.scheduleWidgetUpdates(context)
                                expanded = false
                            }
                        ) {
                            Text(text = "Сохранить")
                        }
                    }
                }
            }
        }
    }
}