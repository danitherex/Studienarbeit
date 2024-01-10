package com.example.studienarbeit

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val locationChannel = NotificationChannel(
                "location",
                "Location Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val geofenceChannel = NotificationChannel(
                "geofence",
                "Geofence Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notifications = mutableListOf(locationChannel,geofenceChannel)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannels(notifications)
        }
    }
}