package com.michaelrayven.lunarcalendar.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.wrapContentHeight
import androidx.glance.layout.wrapContentSize
import androidx.glance.layout.wrapContentWidth
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.michaelrayven.lunarcalendar.MainActivity
import com.michaelrayven.lunarcalendar.R
import com.michaelrayven.lunarcalendar.remote.AppClient
import com.michaelrayven.lunarcalendar.types.LunarDay
import kotlinx.coroutines.launch

class CalendarWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file), Context.MODE_PRIVATE)
        val timeZone = sharedPref.getString(context.getString(R.string.saved_time_zone), "Asia/Novosibirsk")

        provideContent {
            var data by remember { mutableStateOf<List<LunarDay>?>(null) }

            LaunchedEffect(key1 = true) {
                launch {
                    data = AppClient().fetchData(timeZone ?: "Asia/Novosibirsk")
                }
            }

            Box(
                modifier = GlanceModifier.background(Color.DarkGray).clickable(actionStartActivity<MainActivity>()),
            ) {
                if ((data != null) && ((data?.size ?: 0) >= 2)) {
                    WidgetContent(data!![1])
                } else {
                    LoadingContent()
                }
            }
        }
    }

    @Composable
    private fun LoadingContent() {
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loading...",
                modifier = GlanceModifier.padding(12.dp),
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 26.sp
                )
            )
        }
    }

    @Composable
    private fun WidgetContent(data: LunarDay) {
        Row(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = data.day.toString(),
                modifier = GlanceModifier.padding(12.dp).wrapContentSize(),
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 26.sp
                )
            )
            LazyColumn(
                modifier = GlanceModifier.fillMaxSize(),
            ) {
                items(
                    count = data.timeData.size
                ) { i ->
                    CalendarItem(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(if (i % 2 == 0) Color.Gray else Color.Gray.copy(alpha = 0.6f)),
                        time = data.timeData[i].time,
                        data = data.timeData[i].data
                    )
                    Spacer(
                        modifier = GlanceModifier.height(4.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun CalendarItem(modifier: GlanceModifier, time: String, data: String) {
        Row(
            modifier = modifier.cornerRadius(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = time,
                modifier = GlanceModifier.padding(4.dp).wrapContentSize(),
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 20.sp
                )
            )
            Text(
                text = data,
                modifier = GlanceModifier.padding(4.dp).wrapContentSize(),
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 16.sp
                )
            )
        }
    }
}