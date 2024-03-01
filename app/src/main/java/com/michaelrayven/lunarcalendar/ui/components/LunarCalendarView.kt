package com.michaelrayven.lunarcalendar.ui.components

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.michaelrayven.lunarcalendar.types.LunarCalendar
import com.michaelrayven.lunarcalendar.types.LunarDay
import com.michaelrayven.lunarcalendar.types.Sign
import com.michaelrayven.lunarcalendar.util.extractSign
import com.michaelrayven.lunarcalendar.util.formatGmt

@Composable
fun LunarCalendarView(
    lunarCalendar: LunarCalendar
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .fillMaxSize()
    ) {
        LunarCalendarHeader(lunarDay = lunarCalendar.currentDay)
        LunarDayView(lunarDay = lunarCalendar.currentDay)
        Spacer(modifier = Modifier.height(16.dp))
        LunarCalendarTableView(calendar = lunarCalendar.calendar)
    }
}

@Composable
fun LunarCalendarHeader(
    lunarDay: LunarDay
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.wrapContentSize(),
            style = MaterialTheme.typography.headlineLarge,
            text = "${lunarDay.lunarDay} лунный день"
        )
        Text(
            modifier = Modifier.wrapContentSize(),
            style = MaterialTheme.typography.titleMedium,
            text = lunarDay.time + ", " + lunarDay.date,
        )
        Text(
            modifier = Modifier.wrapContentWidth(),
            style = MaterialTheme.typography.titleMedium,
            text = "${lunarDay.location.displayName} (${formatGmt(lunarDay.location.gmt)})",
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun LunarDayView(lunarDay: LunarDay) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        Spacer(modifier = Modifier.height(8.dp))

        LunarDayInfo(lunarDay, lunarDay.sign)
    }
}

@Composable
fun LunarDayInfo(
    lunarDay: LunarDay,
    sign: Sign?
) {
    LunarDayInfoRow("Фаза:", lunarDay.currentLunarPhase)

    sign?.let {
        LunarDayInfoRow("Знак: ", sign.extra, painterResource(id = sign.iconResId))
    }

    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

    LunarDayInfoRow("Период:", lunarDay.period)

    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

    LunarDayInfoRow("Восход:", lunarDay.sunrise)

    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

    LunarDayInfoRow("Закат:", lunarDay.sunset)

    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

    LunarDayInfoRow("Видимость:", lunarDay.visibility)

    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

    LunarDayInfoRow("Расстояние:", lunarDay.distance)

    lunarDay.nextPhases.map { (phase, data) ->
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        LunarDayInfoRow(phase, data)
    }
}

@Composable
fun LunarDayInfoRow(
    title: String,
    data: String,
    icon: Painter? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            modifier = Modifier.weight(.3f, true),
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            modifier = Modifier.weight(.7f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 12.dp),
                    painter = icon,
                    contentDescription = ""
                )
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = data,
                style = MaterialTheme.typography.bodyMedium
            )
        }

    }
}

@Composable
fun LunarCalendarTableView(calendar: List<LunarCalendar.DayData>) {
    Surface(
        modifier = Modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                text = "Лунный календарь:\n${calendar.first().date} - ${calendar.last().date}"
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

            calendar.mapIndexed { index, item ->
                LunarCalendarRow(
                    modifier = Modifier.background(
                        if (index % 2 == 0) {
                            Color.Gray.copy(.05f)
                        } else {
                            Color.Unspecified
                        }
                    ),
                    data = item
                )

                if (index != calendar.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }

}

@Composable
fun LunarCalendarRow(modifier: Modifier = Modifier, data: LunarCalendar.DayData) {
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

        TimeTable(
            modifier = Modifier.weight(.8f),
            table = data.timeTable
        )
    }
}

@Composable
fun TimeTable(
    modifier: Modifier = Modifier,
    table: List<LunarCalendar.TimeData>
) {
    Column(
        modifier = modifier
    ) {
        table.mapIndexed { index, item ->
            val (itemData, sign) = extractSign(item.data)

            Row(
                modifier = Modifier
                    .background(
                        if (index % 2 == 0) {
                            Color.Gray.copy(.15f)
                        } else {
                            Color.Gray.copy(.05f)
                        }
                    )
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.time.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .wrapContentWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier.wrapContentWidth(),
                            text = item.time
                        )

                        if (sign != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Image(
                                modifier = Modifier.size(16.dp),
                                painter = painterResource(sign.iconResId),
                                contentDescription = sign.name
                            )
                        }
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