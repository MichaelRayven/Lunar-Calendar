package com.michaelrayven.lunarcalendar.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.michaelrayven.lunarcalendar.R
import com.michaelrayven.lunarcalendar.ui.components.AppScaffold
import com.michaelrayven.lunarcalendar.ui.components.ItemPicker
import com.michaelrayven.lunarcalendar.ui.components.LocationPicker
import com.michaelrayven.lunarcalendar.ui.components.rememberItemPickerState
import com.michaelrayven.lunarcalendar.ui.components.rememberLocationPickerState
import com.michaelrayven.lunarcalendar.util.formatGmt
import com.michaelrayven.lunarcalendar.util.formatInterval
import com.michaelrayven.lunarcalendar.util.getIntervalFromString
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

@Composable
private fun SettingWidgets() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val interval = remember { mutableLongStateOf(getSavedUpdateInterval(context)) }
    var expanded by remember { mutableStateOf(false) }

    Text(
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.titleMedium,
        text = "Виджеты"
    )

    Spacer(
        modifier = Modifier.height(16.dp)
    )

    Text(
        text = "Частота обновления виджетов:",
        style = MaterialTheme.typography.titleSmall
    )

    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "Текущий интервал: ${formatInterval(interval.longValue)}\nМинимум раз в 15 минут.",
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.bodyMedium
    )

    Spacer(
        modifier = Modifier.height(8.dp)
    )

    TimeIntervalPicker(
        onDismissRequest = { expanded = false },
        onSubmitRequest = { hours, minutes ->
            saveUpdateInterval(context, hours, minutes)
            WidgetUpdateWorker.scheduleWidgetUpdates(context)
            interval.longValue = getIntervalFromString(hours, minutes)
            expanded = false
        },
        state = interval,
        expanded = expanded,
        openButton = {
            Button(
                modifier = Modifier.wrapContentSize(),
                onClick = { expanded = true }
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "Изменить интервал"
                )
            }
        }
    )

    Spacer(
        modifier = Modifier.height(16.dp)
    )

    Text(
        text = "Обновление виджетов вручную:",
        style = MaterialTheme.typography.titleSmall,
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

@Composable
private fun TimeIntervalPicker(
    openButton: @Composable () -> Unit,
    expanded: Boolean,
    state: MutableLongState,
    onDismissRequest: () -> Unit,
    onSubmitRequest: (String, String) -> Unit
) {
    openButton()

    if (expanded) {
        Dialog(
            onDismissRequest = onDismissRequest,
        ) {
            Surface(
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val hourValues = (0..23).map { it.toString().padStart(2, '0') }
                    val hourPickerState = rememberItemPickerState("00")

                    val minuteValues = (0..59).map { it.toString().padStart(2, '0') }
                    val minutePickerState = rememberItemPickerState("00")

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Текущий интервал: ${formatInterval(state.longValue)}",
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Новый интервал: ${
                            formatInterval((hourPickerState.value.toLong() * 60 + minutePickerState.value.toLong()) * 60 * 1000)
                        }",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyMedium,
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
                            ItemPicker(
                                state = hourPickerState,
                                items = hourValues,
                                modifier = Modifier.fillMaxWidth(),
                                textModifier = Modifier.padding(8.dp),
                                textStyle = TextStyle(fontSize = 16.sp),
                                dividerColor = MaterialTheme.colorScheme.outline
                            )
                        }

                        Column(
                            modifier = Modifier.weight(0.7f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Минуты"
                            )
                            ItemPicker(
                                state = minutePickerState,
                                items = minuteValues,
                                modifier = Modifier.fillMaxWidth(),
                                textModifier = Modifier.padding(8.dp),
                                textStyle = TextStyle(fontSize = 16.sp),
                                dividerColor = MaterialTheme.colorScheme.outline
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
                            onSubmitRequest(hourPickerState.value, minutePickerState.value)
                        }
                    ) {
                        Text(text = "Сохранить")
                    }
                }
            }
        }
    }
}