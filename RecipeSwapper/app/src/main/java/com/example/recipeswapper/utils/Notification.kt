package com.example.recipeswapper.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.recipeswapper.R

fun showBadgeNotification(context: Context, message: String) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channel = NotificationChannel(
        "badge_channel",
        "Badge Unlocks",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Notifiche per i badge sbloccati"
    }
    notificationManager.createNotificationChannel(channel)

    val notification = NotificationCompat.Builder(context, "badge_channel")
        .setSmallIcon(R.drawable.cake)
        .setContentTitle("Hai sbloccato un badge!")
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    notificationManager.notify(message.hashCode(), notification)
}

