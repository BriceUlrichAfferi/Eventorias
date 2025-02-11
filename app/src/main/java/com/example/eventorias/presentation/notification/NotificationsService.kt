package com.example.eventorias.presentation.notification


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationsService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (!areNotificationsEnabled()) {
            Log.d("FCM Message", "Notifications are disabled, ignoring message.")
            return
        }

        Log.d("FCM Message", "From: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM Data", remoteMessage.data.toString())
        }

        remoteMessage.notification?.let {
            Log.d("FCM Notification", "Title: ${it.title}, Body: ${it.body}")
            sendNotification(it.title ?: "New Notification", it.body)
        }
    }

    private fun areNotificationsEnabled(): Boolean {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("notifications_enabled", false)
    }

    fun sendNotification(title: String, messageBody: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "default_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for app notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}
