package com.michaelrayven.lunarcalendar.widget

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentSize
import androidx.glance.layout.wrapContentWidth
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.michaelrayven.lunarcalendar.MainActivity
import com.michaelrayven.lunarcalendar.R
import com.michaelrayven.lunarcalendar.types.LunarCalendar
import com.michaelrayven.lunarcalendar.util.extractSign
import com.michaelrayven.lunarcalendar.util.getCachedLunarCalendar
import com.michaelrayven.lunarcalendar.util.getCurrentLunarCalendar
import com.michaelrayven.lunarcalendar.util.getSavedLocation

class CalendarWidget : GlanceAppWidget() {

    companion object {
        private val SMALL_SQUARE = DpSize(100.dp, 100.dp)
        private val HORIZONTAL_RECTANGLE = DpSize(250.dp, 100.dp)
        private val BIG_SQUARE = DpSize(250.dp, 250.dp)
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(
            SMALL_SQUARE,
            HORIZONTAL_RECTANGLE,
            BIG_SQUARE
        )
    )


    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val lunarCalendar = getCachedLunarCalendar(context) ?: getCurrentLunarCalendar(getSavedLocation(context))

        provideContent {
            val size = LocalSize.current

            Box(
                modifier = GlanceModifier
                    .background(ImageProvider(R.drawable.widget_background))
                    .fillMaxSize()
                    .padding(8.dp)
                    .clickable(actionStartActivity<MainActivity>())
            ) {
                if (lunarCalendar != null) {
                    if (size.width >= HORIZONTAL_RECTANGLE.width) {
                        WidgetDefaultView(size = size, lunarCalendar = lunarCalendar)
                    } else {
                        WidgetSmallView(size = size, lunarCalendar = lunarCalendar)
                    }
                } else {
                    WidgetLoading()
                }
            }
        }
    }

    @Composable
    private fun WidgetLoading() {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Загрузка...",
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 20.sp
                )
            )
            Spacer(
                modifier = GlanceModifier.height(16.dp)
            )
            CircularProgressIndicator(
                modifier = GlanceModifier.size(48.dp),
                color = ColorProvider(MaterialTheme.colorScheme.secondary)
            )
        }
    }

    @Composable
    private fun WidgetDefaultView(size: DpSize, lunarCalendar: LunarCalendar) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
        ) {
            Column(
                modifier = GlanceModifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = GlanceModifier.fillMaxWidth(),
                    text = "${lunarCalendar.currentDay.lunarDay} лунный день",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    )
                )
                val startedAt =
                    lunarCalendar.currentDay.period.split(Regex("[()]", RegexOption.MULTILINE))[1]
                Text(
                    modifier = GlanceModifier.fillMaxWidth(),
                    text = "С $startedAt",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = ColorProvider(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                )
            }

            if (size.width >= BIG_SQUARE.width) {
                val todayTimeTable =
                    lunarCalendar.calendar.find { it.day == lunarCalendar.currentDay.day }?.timeTable

                todayTimeTable?.let {
                    Spacer(
                        modifier = GlanceModifier.height(8.dp)
                    )

                    Spacer(
                        modifier = GlanceModifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.outline)
                    )

                    WidgetTimeTable(
                        modifier = GlanceModifier.defaultWeight(),
                        table = it
                    )
                }
            }
        }
    }

    @Composable
    private fun WidgetSmallView(size: DpSize, lunarCalendar: LunarCalendar) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = GlanceModifier.wrapContentSize(),
                text = "${lunarCalendar.currentDay.lunarDay}",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp
                )
            )
            Text(
                modifier = GlanceModifier.wrapContentSize(),
                text = "Лунный день",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(
                modifier = GlanceModifier.height(8.dp)
            )
            val startedAt =
                lunarCalendar.currentDay.period.split(Regex("[()]", RegexOption.MULTILINE))[1]
            Text(
                modifier = GlanceModifier.wrapContentSize(),
                text = "С $startedAt",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = ColorProvider(MaterialTheme.colorScheme.onSurfaceVariant)
                )
            )
        }
    }
}

@Composable
fun WidgetTimeTable(
    modifier: GlanceModifier = GlanceModifier,
    table: List<LunarCalendar.TimeData>
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(32.dp)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = GlanceModifier.width(40.dp),
                text = "Время:",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                )
            )

            Spacer(
                modifier = GlanceModifier.width(8.dp)
            )

            Text(
                modifier = GlanceModifier.defaultWeight(),
                text = "Лунный день, знак, фаза:",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                )
            )
        }
        table.mapIndexed { index, item ->
            WidgetTimeTableRow(
                modifier = GlanceModifier.background(
                    if (index % 2 == 0) {
                        Color.Gray.copy(.1f)
                    } else {
                        Color.Gray.copy(.2f)
                    }
                ),
                time = item.time,
                data = item.data
            )
        }
    }
}

@Composable
fun WidgetTimeTableRow(
    modifier: GlanceModifier = GlanceModifier,
    time: String,
    data: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (text, sign) = extractSign(data)

        if (time.isNotEmpty()) {
            Column(
                modifier = GlanceModifier.padding(end = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = GlanceModifier.wrapContentWidth(),
                    text = time,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.SansSerif
                    )
                )


                if (sign != null) {
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Image(
                        modifier = GlanceModifier.size(16.dp),
                        provider = ImageProvider(sign.iconResId),
                        contentDescription = sign.name
                    )
                }
            }
        }

        Text(
            modifier = GlanceModifier.defaultWeight(),
            text = text,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif
            )
        )
    }
}
