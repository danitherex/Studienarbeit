package com.example.studienarbeit

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private val NOTIFICATION_GROUP_KEY_GEOFENCE = "com.example.studienarbeit.GEOFENCE_NOTIFICATIONS"
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent: GeofencingEvent? = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent?.hasError() == true || geofencingEvent == null) {
            Log.d("GEOFENCE", "Error receiving geofence event..."+ geofencingEvent?.errorCode)
            return
        }

        geofencingEvent.triggeringGeofences?.forEach { geofence ->
            val transition = geofence.transitionTypes
            val requestId = geofence.requestId

            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Log.d("GEOFENCE", "GEOFENCE_TRANSITION_ENTER")
                with(NotificationManagerCompat.from(context)) {
                    if (
                        ActivityCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.POST_NOTIFICATIONS

                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val notification = createNotification(context,requestId)
                        notify(2, notification)
                    }
                }
            } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                Log.d("GEOFENCE", "GEOFENCE_TRANSITION_EXIT")
            }
        }

    }

    private fun createNotification(context: Context,id:String): Notification {
        return NotificationCompat.Builder(context, "geofence")
            .setContentTitle("Entered Geofence")
            .setContentText("Oh no, you entered the geofence $id! Run!")
            .setSmallIcon(R.drawable.burger)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setGroup(NOTIFICATION_GROUP_KEY_GEOFENCE)
            .build()

    }
}
