package com.michaelrayven.lunarcalendar.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.michaelrayven.lunarcalendar.R
import com.michaelrayven.lunarcalendar.types.LunarCalendar
import com.michaelrayven.lunarcalendar.types.LunarDay
import com.michaelrayven.lunarcalendar.ui.components.LoadingSpinner
import com.michaelrayven.lunarcalendar.util.extractSign
import com.michaelrayven.lunarcalendar.util.formatGmt
import com.michaelrayven.lunarcalendar.util.getCurrentLunarCalendar
import com.michaelrayven.lunarcalendar.util.getSavedLocation
import com.michaelrayven.lunarcalendar.work.WidgetUpdateWorker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val location = getSavedLocation(context)

    var lunarCalendar by remember { mutableStateOf<LunarCalendar?>(null) }
    LaunchedEffect(location) {
        lunarCalendar = getCurrentLunarCalendar(location)
    }

    LaunchedEffect(true) {
        WidgetUpdateWorker.scheduleWidgetUpdates(context)
    }

    val state = rememberPullToRefreshState()
    Box(Modifier.nestedScroll(state.nestedScrollConnection)) {
        if (state.isRefreshing) {
            LaunchedEffect(true) {
                lunarCalendar = null
                lunarCalendar = getCurrentLunarCalendar(location)
                state.endRefresh()
            }
        }

        if (lunarCalendar != null) {
            LunarDayView(lunarCalendar = lunarCalendar!!)
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.wrapContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        style = MaterialTheme.typography.titleMedium,
                        text = "Загрузка..."
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LoadingSpinner(modifier = Modifier.size(48.dp))
                }
            }
        }

        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = state
        )
    }
}

@Composable
private fun TableRow(title: String, data: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f, true),
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            modifier = Modifier.weight(2f),
            text = data,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun LunarDayView(lunarCalendar: LunarCalendar) {
    val lunarDay = lunarCalendar.currentDay
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CalendarHeader(lunarDay)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 64.dp, vertical = 16.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(CircleShape),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(lunarDay.imageUrl)
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.FillBounds,
                contentDescription = "Moon image"
            )
        }
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            text = "${lunarDay.lunarDay} лунный день"
        )

        Spacer(modifier = Modifier.height(8.dp))

        TableRow("Фаза:", lunarDay.currentLunarPhase)

        if (lunarDay.sign != null) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.weight(1f, true),
                    text = "Знак:",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    modifier = Modifier.weight(2f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 12.dp),
                        painter = painterResource(id = lunarDay.sign.iconResId),
                        contentDescription = "Zodiac sign image"
                    )
                    Text(
                        text = lunarDay.sign.extra,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        TableRow("Период:", lunarDay.period)

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        TableRow("Восход:", lunarDay.sunrise)

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        TableRow("Закат:", lunarDay.sunset)

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        TableRow("Видимость:", lunarDay.visibility)

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        TableRow("Расстояние:", lunarDay.distance)

        lunarDay.nextPhases.map { (phase, data) ->
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            TableRow(phase, data)
        }

        TimeTable(data = lunarCalendar.calendar)
    }
}

@Composable
private fun CalendarHeader(lunarDay: LunarDay) {
    Column {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            text = "Лунный календарь"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                text = "на ${lunarDay.date}",
            )
            DateTimePicker()
        }

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            text = "${lunarDay.location.displayName} (${formatGmt(lunarDay.location.gmt)})",
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker() {
    val timePickerState = rememberTimePickerState()
    val datePickerState = rememberDatePickerState()
    val showDatePicker = rememberSaveable { mutableStateOf(false) }
    val showTimePicker = rememberSaveable { mutableStateOf(false) }

    IconButton(onClick = { showDatePicker.value = true }) {
        Icon(imageVector = Icons.Filled.DateRange, contentDescription = "")
    }

    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker.value = false
                    showTimePicker.value = true
                }) {
                    Text("Ок")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker.value = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker.value) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker.value = false },
            confirmButton = {
                TextButton(onClick = { showTimePicker.value = false }) {
                    Text("Ок")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker.value = false }) {
                    Text("Отмена")
                }
            },
            state = timePickerState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    state: TimePickerState,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    shape: Shape = DatePickerDefaults.shape,
    tonalElevation: Dp = DatePickerDefaults.TonalElevation,
    colors: DatePickerColors = DatePickerDefaults.colors(),
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
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
        shape = shape,
        tonalElevation = tonalElevation,
        colors = colors,
        properties = properties,
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
                        "Select time"
                    } else {
                        "Enter time"
                    },
                    style = MaterialTheme.typography.labelLarge
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


@Composable
fun TimeTable(data: List<LunarCalendar.DayData>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                text = "Лунный календарь:\n${data.first().date} - ${data.last().date}"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text(
                    modifier = Modifier.weight(.2f),
                    text = "Дата",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                VerticalDivider(
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    modifier = Modifier.weight(.8f),
                    text = "Лунный день, знак, Луна без курса, фаза",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            HorizontalDivider()

            data.mapIndexed { index, item ->
                TimeTableRow(
                    modifier = Modifier.background(
                        if (index % 2 == 0) {
                            Color.Gray.copy(.1f)
                        } else {
                            Color.Gray.copy(.2f)
                        }
                    ),
                    data = item
                )

                if (index != data.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }

}

@Composable
fun TimeTableRow(modifier: Modifier = Modifier, data: LunarCalendar.DayData) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(.2f),
            text = "${data.day}/${data.month}\n${data.year}",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            modifier = Modifier.weight(.8f)
        ) {
            data.timeTable.mapIndexed { index, item ->
                val (itemData, sign) = extractSign(item.data)

                Row(
                    modifier = Modifier
                        .background(
                            if (index % 2 == 0) {
                                Color.Gray.copy(.075f)
                            } else {
                                Color.Gray.copy(.15f)
                            }
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(horizontal = 8.dp),
                            text = item.time
                        )
                        if (sign != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Image(
                                modifier = Modifier.size(16.dp),
                                painter = painterResource(sign.iconResId),
                                contentDescription = sign.name
                            )
                        }
                    }

                    Text(
                        modifier = Modifier.weight(1f),
                        text = itemData
                    )
                }
            }
        }
    }
}