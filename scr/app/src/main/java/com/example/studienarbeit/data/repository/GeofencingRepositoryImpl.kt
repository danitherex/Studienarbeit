package com.example.studienarbeit.data.repository

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.studienarbeit.GeofenceBroadcastReceiver
import com.example.studienarbeit.domain.model.MarkerModel
import com.example.studienarbeit.domain.repository.GeofencingRepository
import com.example.studienarbeit.settings.datastore
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject


class GeofencingRepositoryImpl @Inject constructor(
    private val context: Context,
    private val geofencingClient: GeofencingClient,
) : GeofencingRepository {
    fun createGeofence(
        latLng: LatLng,
        radius: Float?,
        transitionTypes: Int,
        id: String,
    ): Geofence {
        return Geofence.Builder()
            .setRequestId(generateRequestId())
            .setCircularRegion(latLng.latitude, latLng.longitude, radius ?: 100f)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(transitionTypes)
            .setRequestId(id)
            //TODO: set expiration duration
            //.setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setExpirationDuration(86400000)
            .build()
    }

    @SuppressLint("MissingPermission")
    //TODO: save current geofences in datastore to retrieve after boot and to not add them again
    override suspend fun setGeofence(markers: List<MarkerModel>) {
        val appstore = context.datastore.data
        val pendingIntent = getGeofencePendingIntent()

        val geofences: List<Geofence>;
        val geofencesToRemove: List<String>;

        val appStoreValues = runBlocking { appstore.first() }
        val geofencesFromAppstore = appStoreValues.geofences
        val radius = appStoreValues.radius.toFloat()

        geofences = markers
            .filter { !geofencesFromAppstore.contains(it.id) }
            .map {
                createGeofence(
                    LatLng(it.position.latitude, it.position.longitude),
                    radius,
                    Geofence.GEOFENCE_TRANSITION_ENTER,
                    it.id
                )
            }

        geofencesToRemove = geofencesFromAppstore
            .filter { geofenceId -> markers.none { it.id == geofenceId } }

        if (geofencesToRemove.isNotEmpty()) {
            geofencingClient.removeGeofences(geofencesToRemove)
                .addOnSuccessListener {
                    CoroutineScope(Dispatchers.Main).launch {

                        context.datastore.updateData { it ->
                            Log.d(
                                "GEOFENCE",
                                "Removed ${geofencesToRemove.size} Geofences from datastore ..."
                            )
                            it.copy(
                                geofences = geofencesFromAppstore.filter { geofenceId ->
                                    markers.any { it.id == geofenceId }
                                }
                            )
                        }
                    }
                    Log.d("GEOFENCE", "Removed ${geofencesToRemove.size} Geofences ...")
                }
                .addOnFailureListener { e ->
                    Log.d("GEOFENCE", "Failed to remove ${geofencesToRemove.size} Geofences: $e")
                }
        }

        if (geofences.isNotEmpty()) {
            val geofencingRequest = GeofencingRequest.Builder()
                .addGeofences(geofences)
                .build()

            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        Log.d("GEOFENCE", "Added ${geofences.size} Geofences to datastore ...")
                        context.datastore.updateData { it ->
                            it.copy(
                                //geofences = geofences.map { it.requestId }
                                geofences = geofences.map { it.requestId } + geofencesFromAppstore.filter { geofenceId ->
                                    geofencesToRemove.none { it == geofenceId }
                                }

                            )
                        }
                    }

                    Log.d("GEOFENCE", "Added ${geofences.size} Geofences")
                }
                .addOnFailureListener { e ->
                    Log.d("GEOFENCE", "Failed to add ${geofences.size} Geofences: $e")
                }
        }
    }


    override suspend fun removeAllGeofences() {
        val pendingIntent = getGeofencePendingIntent()
        geofencingClient.removeGeofences(pendingIntent)
            .addOnSuccessListener {
                CoroutineScope(Dispatchers.Main).launch {

                    context.datastore.updateData {
                        it.copy(
                            geofences = emptyList()
                        )
                    }
                }
                Log.d(
                    "GEOFENCE",
                    "Removed all Geofences ..."
                )
            }
            .addOnFailureListener { e ->
                Log.d(
                    "GEOFENCE",
                    "Failed to remove all Geofences: $e"
                )
            }
    }


    private fun getGeofencePendingIntent(): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    private fun generateRequestId(): String {
        return UUID.randomUUID().toString()
    }


}