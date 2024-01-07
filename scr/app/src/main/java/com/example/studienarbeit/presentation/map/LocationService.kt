package com.example.studienarbeit.presentation.map

import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.studienarbeit.R
import com.example.studienarbeit.domain.repository.LocationRepository
import com.example.studienarbeit.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Inject
    lateinit var locationClient: LocationRepository

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {

        val resultIntent = Intent(this, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Location Service")
            .setContentText("Location Service is running")
            .setSmallIcon(R.drawable.burger)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setContentIntent(resultPendingIntent)


        //val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        locationClient.requestLocationUpdates(1000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                Log.d("LocationService", "start: $location")
                //val lat = location?.latitude.toString()
                //val lng = location?.longitude.toString()
                //val updatedNotification = notification.setContentText("Location Service is running : $lat,$lng")
                //notificationManager.notify(1,updatedNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())

    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

}