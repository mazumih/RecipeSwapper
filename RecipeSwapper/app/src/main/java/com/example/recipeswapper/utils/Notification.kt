package com.example.recipeswapper.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.recipeswapper.R

class NotificationHelper(
    private val ctx: Context
) {
    val notificationManager = ctx.getSystemService(NotificationManager::class.java)

    init {
        val channel = NotificationChannel(
            "recipeswapper_channel",
            "RecipeSwapper Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Nuova notifica"
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun notify(title: String, message: String) {
        val notification = NotificationCompat.Builder(ctx, "recipeswapper_channel")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.icona)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(message.hashCode(), notification)
    }
}