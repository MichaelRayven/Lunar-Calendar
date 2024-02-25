package com.michaelrayven.lunarcalendar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await
import com.michaelrayven.lunarcalendar.MainActivity.Companion.UPDATE_WIDGET_TAG
import com.michaelrayven.lunarcalendar.types.TimeZoneInfo
import com.michaelrayven.lunarcalendar.ui.theme.LunarCalendarTheme
import com.michaelrayven.lunarcalendar.work.WidgetUpdateWorker
import kotlinx.coroutines.launch
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val scope = rememberCoroutineScope()
            var widgetScheduledUpdateWorkInfo by remember { mutableStateOf<WorkInfo?>(null) }

            val sharedPref = getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)

            LaunchedEffect(key1 = true) {
                launch {
                    scheduleWidgetUpdates(applicationContext)
                    widgetScheduledUpdateWorkInfo = getScheduledWorkInfo(applicationContext)
                }
            }

            LunarCalendarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    TimeZoneDropdown("") {
                        with (sharedPref.edit()) {
                            putString(getString(R.string.saved_time_zone), it.id)
                            apply()
                        }

                        scope.launch {
                            updateWidget(applicationContext)
                        }
                    }
                    
                    Column {
                        Text(text = "Widget Update Work Status: ${!(widgetScheduledUpdateWorkInfo?.state?.isFinished ?: true)}")
                        Button(
                            modifier = Modifier.wrapContentSize(),
                            onClick = {
                                scope.launch {
                                    updateWidget(applicationContext)
                                }
                            }) {
                            Text(
                                text = "Force Update Widget"
                            )
                        }
                        Button(
                            modifier = Modifier.wrapContentSize(),
                            onClick = {
                                scope.launch {
                                    scheduleWidgetUpdates(applicationContext)
                                    widgetScheduledUpdateWorkInfo = getScheduledWorkInfo(applicationContext)
                                }
                            }) {
                            Text(
                                text = "Schedule Widget Update"
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val UPDATE_WIDGET_TAG = "update-widget"
    }
}

suspend fun updateWidget(context: Context) {
    val widgetSingleUpdateRequest = OneTimeWorkRequestBuilder<WidgetUpdateWorker>().build()
    val wordManager = WorkManager
        .getInstance(context)

    wordManager
        .enqueue(widgetSingleUpdateRequest)
        .await()
}

suspend fun scheduleWidgetUpdates(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .build()
    val widgetUpdateRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(1, TimeUnit.DAYS)
        .setConstraints(constraints)
        .addTag(UPDATE_WIDGET_TAG)
        .build()
    val workManger = WorkManager
        .getInstance(context)

    workManger
        .cancelAllWorkByTag(UPDATE_WIDGET_TAG)
        .await()

    workManger
        .enqueue(widgetUpdateRequest)
        .await()
}

@SuppressLint("RestrictedApi")
suspend fun getScheduledWorkInfo(context: Context): WorkInfo? {
    val workManger = WorkManager
        .getInstance(context)

    val infoList = workManger
        .getWorkInfosByTag(UPDATE_WIDGET_TAG)
        .await()


    return infoList.getOrNull(0)
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeZoneDropdown(
    selected: String,
    onChangeSelection: (TimeZoneInfo) -> Unit
) {
    val timeZones = getTimeZones()

    var selectedText by remember { mutableStateOf(selected) }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = Modifier.wrapContentSize(),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedText,
            onValueChange = { value -> selectedText = value },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            LazyColumn(
                modifier = Modifier.width(500.dp).height(150.dp)
            ) {
                val filteredTimeZones = timeZones.filter { it.name.contains(selectedText, true) }
                items(filteredTimeZones.size) {index ->
                    val item = filteredTimeZones[index]
                    TimeZoneItem(item) { tz ->
                        expanded = false
                        selectedText = tz.name
                        onChangeSelection(tz)
                    }
                }
            }
        }
    }
}

@Composable
fun TimeZoneItem(tz: TimeZoneInfo, onClick: (TimeZoneInfo) -> Unit) {
    DropdownMenuItem(text = {
        Text(
            text = tz.name,
            fontSize = 16.sp,
        )
    }, onClick = { onClick(tz) })
}

fun getTimeZones(): List<TimeZoneInfo> {
    return TimeZone.getAvailableIDs().map { id ->
        val tokens = id.replace("_", " ").split("/")

        val name = if (tokens.size == 2) {
            val (country, city) = tokens
            "$country, $city"
        } else return@map null

        if (name.contains("etc", true)) return@map null

        TimeZoneInfo(
            name = name,
            id = id
        )
    }.filterNotNull()
}
