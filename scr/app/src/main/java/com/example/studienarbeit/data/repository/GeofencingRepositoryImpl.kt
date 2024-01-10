package com.example.studienarbeit.data.repository

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.studienarbeit.GeofenceBroadcastReceiver
import com.example.studienarbeit.domain.repository.GeofencingRepository
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng
import java.util.UUID
import javax.inject.Inject


class GeofencingRepositoryImpl @Inject constructor(
    private val context: Context,
    private val geofencingClient: GeofencingClient
) : GeofencingRepository {
    override fun createGeofence(latLng: LatLng, radius: Float, transitionTypes: Int,id:String): Geofence {
        return Geofence.Builder()
            .setRequestId(generateRequestId())
            .setCircularRegion(latLng.latitude, latLng.longitude, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(transitionTypes)
            .setRequestId(id)
            //TODO: set expiration duration
            //.setExpirationDuration(Geofence.NEVER_EXPIRE)
            //setExpiration duration to 1 day
            .setExpirationDuration(86400000)
            .build()
    }

    @SuppressLint("MissingPermission")
    //TODO: save current geofences in datastore to retrieve after boot and to not add them again
    override fun setGeofence(geofences:MutableList<Geofence>) {
        val geofencingRequest = GeofencingRequest.Builder()
            .addGeofences(geofences)
            .build()
        val pendingIntent = getGeofencePendingIntent()
        geofencingClient.removeGeofences(pendingIntent)
            .addOnSuccessListener { Log.d("GEOFENCE", "Geofences Removed...") }
            .addOnFailureListener { e -> Log.d("GEOFENCE", "Failed to remove Geofences: $e") }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener { Log.d("GEOFENCE", "Added ${geofences.size} Geofences") }
            .addOnFailureListener { e -> Log.d("GEOFENCE", "Failed to add Geofences: $e") }
    }



    private fun getGeofencePendingIntent(): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
    }

    private fun generateRequestId(): String {
        return UUID.randomUUID().toString()
    }
}