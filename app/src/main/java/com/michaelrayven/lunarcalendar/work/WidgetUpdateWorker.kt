package com.michaelrayven.lunarcalendar.work

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.michaelrayven.lunarcalendar.widget.CalendarWidget

class WidgetUpdateWorker(
    private val appContext: Context,
    private val params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return try {
            CalendarWidget().updateAll(appContext)
            Result.success()
        } catch (error: Exception) {
            Result.failure()
        }
    }
}