package com.michaelrayven.lunarcalendar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.michaelrayven.lunarcalendar.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    modifier: Modifier = Modifier,
    state: TimePickerState,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
) {
    val showingPicker = remember { mutableStateOf(true) }
    val configuration = LocalConfiguration.current

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    modifier = Modifier.padding(start = 12.dp),
                    onClick = { showingPicker.value = !showingPicker.value }
                ) {
                    val icon = if (showingPicker.value) {
                        painterResource(id = R.drawable.baseline_keyboard_24)
                    } else {
                        painterResource(id = R.drawable.baseline_access_time_24)
                    }
                    Icon(
                        painter = icon,
                        contentDescription = if (showingPicker.value) {
                            "Switch to Text Input"
                        } else {
                            "Switch to Touch Input"
                        }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                dismissButton?.invoke()
                confirmButton()
            }
        },
        modifier = modifier,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 12.dp, top = 16.dp, bottom = 24.dp),
                    text = if (showingPicker.value) {
                        "Выберите время"
                    } else {
                        "Введите время"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                if (showingPicker.value && configuration.screenHeightDp > 400) {
                    TimePicker(state = state)
                } else {
                    TimeInput(state = state)
                }
            }
        }
    )
}