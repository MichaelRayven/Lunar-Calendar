package com.michaelrayven.lunarcalendar.work

import android.annotation.SuppressLint
import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.await
import com.michaelrayven.lunarcalendar.R
import com.michaelrayven.lunarcalendar.util.cacheLunarCalendar
import com.michaelrayven.lunarcalendar.util.getCurrentLunarCalendar
import com.michaelrayven.lunarcalendar.util.getSavedLocation
import com.michaelrayven.lunarcalendar.widget.CalendarWidget
import java.util.concurrent.TimeUnit

class WidgetUpdateWorker(
    private val appContext: Context,
    private val params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val lunarCalendar = getCurrentLunarCalendar(getSavedLocation(appContext)) ?: return Result.failure()

        cacheLunarCalendar(appContext, lunarCalendar)

        return try {
            CalendarWidget().updateAll(appContext)
            Result.success()
        } catch (error: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val UPDATE_WIDGET_TAG = "update-widget"

        fun updateWidget(context: Context): Operation {
            val widgetSingleUpdateRequest = OneTimeWorkRequestBuilder<WidgetUpdateWorker>().build()
            val wordManager = WorkManager
                .getInstance(context)

            return wordManager.enqueue(widgetSingleUpdateRequest)
        }

        fun scheduleWidgetUpdates(context: Context): Operation {
            val preferencesFile = context.getString(R.string.preference_file)
            val preferences = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)

            val (hours, minutes) = preferences
                .getString(context.getString(R.string.saved_update_interval), "00:00")!!
                .split(":")
                .map { it.toInt() }

            var duration: Long = (hours * 60 + minutes).toLong()

            if (duration == 0.toLong()) {
                duration = 24 * 60
            }

            val widgetUpdateRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(duration.coerceAtLeast(15), TimeUnit.MINUTES)
                .addTag(UPDATE_WIDGET_TAG)
                .build()
            val workManger = WorkManager
                .getInstance(context)

            workManger.cancelAllWorkByTag(UPDATE_WIDGET_TAG)

            return workManger.enqueue(widgetUpdateRequest)
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

    }
}