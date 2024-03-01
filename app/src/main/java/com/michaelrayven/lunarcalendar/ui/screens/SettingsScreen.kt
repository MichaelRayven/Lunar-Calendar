package com.michaelrayven.lunarcalendar.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.michaelrayven.lunarcalendar.ui.components.AppScaffold
import com.michaelrayven.lunarcalendar.ui.components.LocationPicker
import com.michaelrayven.lunarcalendar.ui.components.TimePickerDialog
import com.michaelrayven.lunarcalendar.ui.components.rememberLocationPickerState
import com.michaelrayven.lunarcalendar.util.formatGmt
import com.michaelrayven.lunarcalendar.util.formatInterval
import com.michaelrayven.lunarcalendar.util.computeInterval
import com.michaelrayven.lunarcalendar.util.getSavedLocation
import com.michaelrayven.lunarcalendar.util.getSavedUpdateInterval
import com.michaelrayven.lunarcalendar.util.saveLocation
import com.michaelrayven.lunarcalendar.util.saveUpdateInterval
import com.michaelrayven.lunarcalendar.work.WidgetUpdateWorker
import com.michaelrayven.lunarcalendar.work.WidgetUpdateWorker.Companion.updateWidget
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    AppScaffold(navController = navController, snackbarHostState = snackbarHostState) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(paddingValues)
        ) {
            SettingLocation()

            Spacer(modifier = Modifier.height(16.dp))

            SettingWidgets()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingWidgets() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val interval = remember { mutableLongStateOf(getSavedUpdateInterval(context)) }

    Text(
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.titleMedium,
        text = "Настройка виджетов:"
    )

    Spacer(
        modifier = Modifier.height(8.dp)
    )

    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "Текущий интервал обновления виджетов:\n${formatInterval(interval.longValue)}." +
                "\nМинимальный интервал раз в 15 минут.",
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.bodyMedium
    )

    Spacer(
        modifier = Modifier.height(8.dp)
    )

    val timePickerState = rememberTimePickerState()
    val showTimePicker = rememberSaveable { mutableStateOf(false) }

    Button(
        modifier = Modifier.wrapContentSize(),
        onClick = { showTimePicker.value = true }
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Изменить интервал"
        )
    }

    if (showTimePicker.value) {
        TimePickerDialog(
            state = timePickerState,
            onDismissRequest = { showTimePicker.value = false },
            confirmButton = {
                TextButton(onClick = {
                    saveUpdateInterval(context, timePickerState.hour, timePickerState.minute)
                    interval.longValue = computeInterval(timePickerState.hour, timePickerState.minute)
                    WidgetUpdateWorker.scheduleWidgetUpdates(context)
                    showTimePicker.value = false
                }) {
                    Text("Ок")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker.value = false }) {
                    Text("Отмена")
                }
            },
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Текущий интервал: ${formatInterval(interval.longValue)}",
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Новый интервал: ${formatInterval(computeInterval(timePickerState.hour, timePickerState.minute).coerceAtLeast(15 * 60 * 1000))}",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Выберите временной интервал",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        )
    }

    Spacer(
        modifier = Modifier.height(16.dp)
    )

    Text(
        text = "Обновление виджетов вручную:",
        style = MaterialTheme.typography.titleMedium,
    )

    Spacer(
        modifier = Modifier.height(8.dp)
    )

    Button(
        modifier = Modifier.wrapContentSize(),
        onClick = {
            scope.launch {
                updateWidget(context)
            }
        }
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Обновить виджеты"
        )
    }
}

@Composable
fun SettingLocation() {
    val context = LocalContext.current
    var location by remember { mutableStateOf(getSavedLocation(context)) }
    val locationPickerState = rememberLocationPickerState()

    Column {
        Text(
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium,
            text = "Смена локации:"
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Текущая локация: ${location.displayName} (${formatGmt(location.gmt)})",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium
        )

        LocationPicker(state = locationPickerState)

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            onClick = {
                saveLocation(context, locationPickerState.value!!)
                location = locationPickerState.value!!
            },
            enabled = locationPickerState.value != null
        ) {
            Text(text = "Сохранить")
        }
    }
}