package com.michaelrayven.lunarcalendar.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.michaelrayven.lunarcalendar.types.Location
import com.michaelrayven.lunarcalendar.util.getSavedLocation
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboPicker(
    expanded: Boolean,
    openButton: (@Composable () -> Unit)? = null,
    onDismissRequest: () -> Unit,
    onSelected: (location: Location, timestamp: Long) -> Unit
) {
    val calendar = Calendar.getInstance()
    val context = LocalContext.current

    openButton?.invoke()

    if (expanded) {
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE)
        )
        val datePickerState =
            rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)
        val locationPickerState = rememberLocationPickerState(default = getSavedLocation(context))
        val step = rememberSaveable { mutableIntStateOf(0) }

        if (step.intValue == 0) {
            DatePickerDialog(
                onDismissRequest = onDismissRequest,
                confirmButton = {
                    TextButton(onClick = {
                        step.intValue = 1
                    }) {
                        Text("Ок")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissRequest) {
                        Text("Отмена")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (step.intValue == 1) {
            TimePickerDialog(
                onDismissRequest = onDismissRequest,
                confirmButton = {
                    TextButton(onClick = {
                        step.intValue = 2
                    }) {
                        Text("Ок")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        step.intValue = 0
                    }) {
                        Text("Назад")
                    }
                },
                state = timePickerState
            )
        }

        if (step.intValue == 2) {
            LocationPickerDialog(
                onDismissRequest = onDismissRequest,
                confirmButton = {
                    TextButton(
                        onClick = {
                            onSelected(
                                locationPickerState.value!!,
                                datePickerState.selectedDateMillis!! + (timePickerState.hour * 60 + timePickerState.minute) * 60 * 1000,
                            )
                        },
                        enabled = locationPickerState.value != null
                    ) {
                        Text("Ок")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        step.intValue = 1
                    }) {
                        Text("Назад")
                    }
                },
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Выбраная локация: ${
                        locationPickerState.value?.displayName ?: listOfNotNull(
                            locationPickerState.country?.name,
                            locationPickerState.state?.name
                        ).joinToString(", ")
                    }",
                    color = MaterialTheme.colorScheme.secondary
                )
                LocationPicker(state = locationPickerState)
            }
        }
    }
}