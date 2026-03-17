package com.alif.dicodingevent.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alif.dicodingevent.R
import com.alif.dicodingevent.data.remote.retrofit.ApiConfig

class DailyReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_NAME = "Dicoding Event Reminder"
        const val CHANNEL_ID = "dec-01"
    }

    override suspend fun doWork(): Result {
        try {
            val response = ApiConfig.getApiService().getEvents(-1, 1)
            val event = response.listEvents.firstOrNull()
            event?.let {
                showNotification(it.name, it.beginTime)
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun showNotification(title: String?, beginTime: String?) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Event Terdekat")
            .setContentText("$title akan dimulai pada $beginTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}