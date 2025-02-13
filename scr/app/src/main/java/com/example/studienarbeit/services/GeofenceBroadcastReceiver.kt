package com.example.studienarbeit.services

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.studienarbeit.R
import com.example.studienarbeit.settings.datastore
import com.example.studienarbeit.utils.Constants.GEOFENCE_CHANNEL_ID
import com.example.studienarbeit.utils.Constants.NOTIFICATION_GROUP_KEY_GEOFENCE
import com.example.studienarbeit.utils.Constants.SUMMARY_ID
import com.example.studienarbeit.utils.Icons
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private var notificationIdCounter = 0
    }

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent: GeofencingEvent? = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent?.hasError() == true || geofencingEvent == null) {
            Log.d("GEOFENCE", "Error receiving geofence event..." + geofencingEvent?.errorCode)
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
                        CoroutineScope(Dispatchers.Main).launch {
                            val activeNotifications = activeNotifications.size
                            val summaryNotification = getSummaryNotification(context,activeNotifications)
                            val notification = createNotification(context, requestId)
                            val notificationId = getNextNotificationId()

                            notify(SUMMARY_ID, summaryNotification)
                            notify(notificationId, notification)
                        }
                    }
                }
            } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                Log.d("GEOFENCE", "GEOFENCE_TRANSITION_EXIT")
            }
        }

    }

    private suspend fun createNotification(context: Context, id: String): Notification {
        val markers = context.datastore.data.first().markers
        val marker = markers.find { it.id == id }
        val icon: Icons = if (marker?.type == null) {
            Icons.RESTAURANT
        } else
            Icons.entries.find { it.name == (marker.type.uppercase()) }
                ?: Icons.RESTAURANT
        val latitude = marker?.position?.latitude
        val longitude = marker?.position?.longitude

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$latitude,$longitude&mode=w"))
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        return NotificationCompat.Builder(context, GEOFENCE_CHANNEL_ID)
            .setContentTitle("${marker?.title} nearby")
            .setContentText(marker?.description)
            .setSmallIcon(icon.resourceId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setGroup(NOTIFICATION_GROUP_KEY_GEOFENCE)
            .addAction(R.drawable.google_map,"Navigate",pendingIntent)
            .build()

    }

    private fun getSummaryNotification(context:Context,activeNotifications:Int):Notification {
        return NotificationCompat.Builder(context, GEOFENCE_CHANNEL_ID)
            .setContentTitle("$activeNotifications locations nearby")
            .setSmallIcon(R.drawable.donut)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setGroup(NOTIFICATION_GROUP_KEY_GEOFENCE)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
            .setGroupSummary(true)
            .build()

    }


    private fun getNextNotificationId(): Int {
        return notificationIdCounter++
    }
}
