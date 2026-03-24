package com.tbart.blackjack.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.tbart.blackjack.R
import com.tbart.blackjack.activity.ConnectionActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        const val CHANNEL_ID = "daily_reminder_channel"
        const val NOTIFICATION_ID = 1001
        const val PREFS_NAME = "blackjack_notification_prefs"
        const val KEY_LAST_OPEN_DATE = "last_open_date"
    }

    override fun doWork(): Result {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastOpenDate = prefs.getString(KEY_LAST_OPEN_DATE, "")
        if (lastOpenDate != today) {
            sendNotification()
        }

        return Result.success()
    }

    private fun sendNotification() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Créer le canal (requis Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Rappel quotidien",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Rappel pour jouer au Blackjack"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, ConnectionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Ton argent t'attend")
            .setContentText("Tu n'as pas joué aujourd'hui ! Viens affronter le méchant croupier.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
