package com.michaelrayven.lunarcalendar.ui.screens

import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.michaelrayven.lunarcalendar.R
import com.michaelrayven.lunarcalendar.remote.AppClient
import com.michaelrayven.lunarcalendar.types.Location
import com.michaelrayven.lunarcalendar.types.LunarDay
import com.michaelrayven.lunarcalendar.ui.components.LoadingSpinner
import kotlinx.serialization.json.Json
import java.util.Calendar

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val preferencesFile = context.getString(R.string.preference_file)
    val preferences = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)
    val location = try {
        Json.decodeFromString<Location>(
            preferences.getString(context.getString(R.string.saved_location), null) ?: ""
        )
    } catch (e: IllegalArgumentException) {
        Location.DEFAULT
    }

    var lunarDay by remember { mutableStateOf<LunarDay?>(null) }
    LaunchedEffect(location) {
        val client = AppClient()
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        lunarDay = client.getLunarDay(location, day, month, year, hour, minute)
    }

    if (lunarDay != null) {
        LunarDayView(lunarDay = lunarDay!!)
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
fun LunarDayView(lunarDay: LunarDay) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            text = "Лунный календарь"
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            text = lunarDay.location.displayName,
            color = MaterialTheme.colorScheme.secondary
        )

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
            Divider(modifier = Modifier.padding(horizontal = 16.dp))

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

        Divider(modifier = Modifier.padding(horizontal = 16.dp))

        TableRow("Период:", lunarDay!!.period)

        Divider(modifier = Modifier.padding(horizontal = 16.dp))

        TableRow("Восход:", lunarDay!!.sunrise)

        Divider(modifier = Modifier.padding(horizontal = 16.dp))

        TableRow("Закат:", lunarDay!!.sunset)

        Divider(modifier = Modifier.padding(horizontal = 16.dp))

        TableRow("Видимость:", lunarDay!!.visibility)

        Divider(modifier = Modifier.padding(horizontal = 16.dp))

        TableRow("Расстояние:", lunarDay!!.distance)

        lunarDay!!.nextPhases.map { (phase, data) ->
            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            TableRow(phase, data)
        }
    }
}


@Composable
fun TimeTable() {

}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}