package com.techpuram.leadandfollowmanagement.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.techpuram.leadandfollowmanagement.MainActivity
import com.techpuram.leadandfollowmanagement.R
class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams)
{

    override fun doWork(): Result {
        return try {
            val followUpId = inputData.getInt("followUpId", -1)
            val title = inputData.getString("title") ?: "Follow-up Reminder"
            val message = inputData.getString("message") ?: "You have a follow-up reminder"

            createNotificationChannel()
            showNotification(followUpId, title, message)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Follow-up Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for follow-up reminders and due dates"
                enableLights(true)
                enableVibration(true)
                setBypassDnd(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(followUpId: Int, title: String, message: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("followUpId", followUpId)
            putExtra("fromNotification", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            followUpId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.follow_up_filled)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)

        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(followUpId, notification)
        }
    }

    companion object {
        private const val CHANNEL_ID = "followup_reminders"
    }
}
