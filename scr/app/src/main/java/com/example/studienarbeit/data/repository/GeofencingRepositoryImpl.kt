package com.example.studienarbeit.data.repository

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.WorkManager
import com.example.studienarbeit.services.GeofenceBroadcastReceiver
import com.example.studienarbeit.domain.model.MarkerModel
import com.example.studienarbeit.domain.repository.GeofencingRepository
import com.example.studienarbeit.domain.use_case.GetLocation
import com.example.studienarbeit.settings.datastore
import com.example.studienarbeit.utils.Constants.GEOFENCE_WORKER_TAG
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


class GeofencingRepositoryImpl @Inject constructor(
    private val context: Context,
    private val geofencingClient: GeofencingClient,
    private val getLocation: GetLocation,
) : GeofencingRepository {
    private fun createGeofence(
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
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    override suspend fun setGeofence(markers: List<MarkerModel>) {
        val currentLocationFlow = getLocation.invoke()
        val currentLocation = currentLocationFlow.firstOrNull()


        val appstore = context.datastore.data
        val pendingIntent = getGeofencePendingIntent()

        var geofences: List<Geofence>
        val geofencesToRemove: List<String>

        val appStoreValues = runBlocking { appstore.first() }
        val geofencesFromAppstore = appStoreValues.geofences
        val radius = appStoreValues.radius.toFloat()

        val nearestMarkers =
            currentLocation?.let { calculateNearestMarkers(it, markers) } ?: markers.take(100)


        geofences = nearestMarkers
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
            .filter { geofenceId -> nearestMarkers.none { it.id == geofenceId } }

        if (geofencesToRemove.isNotEmpty()) {
            geofencingClient.removeGeofences(geofencesToRemove)
                .addOnSuccessListener {
                    CoroutineScope(Dispatchers.IO).launch {

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
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.d("GEOFENCE", "Added ${geofences.size} Geofences to datastore ...")
                        context.datastore.updateData { it ->
                            it.copy(
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

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")//TODO: check if permission is missing
    override suspend fun addMarkersAsGeofences() {
        val appstore = context.datastore.data
        val pendingIntent = getGeofencePendingIntent()

        val currentLocationFlow = getLocation.invoke()
        val currentLocation = currentLocationFlow.firstOrNull()

        val markers = appstore.first().markers
        val radius = appstore.first().radius.toFloat()

        val nearestMarkers =
            currentLocation?.let { calculateNearestMarkers(it, markers) } ?: markers.take(100)

        val geofences = nearestMarkers
            .map {
                createGeofence(
                    LatLng(it.position.latitude, it.position.longitude),
                    radius,
                    Geofence.GEOFENCE_TRANSITION_ENTER,
                    it.id
                )
            }

        geofencingClient.removeGeofences(pendingIntent)
            .addOnSuccessListener {
                Log.d("GEOFENCE", "Removed all Geofences ...")
                CoroutineScope(Dispatchers.Main).launch {
                    context.datastore.updateData { it ->
                        it.copy(
                            geofences = emptyList()
                        )
                    }
                }
            }
            .addOnFailureListener { e -> Log.d("GEOFENCE", "Failed to remove all Geofences: $e") }

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
                                geofences = geofences.map {
                                    it.requestId
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
                cancelGeofenceUpdateWorker()
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

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    override suspend fun updateRadius(radius: Float) {
        val appstore = context.datastore.data
        val pendingIntent = getGeofencePendingIntent()

        val currentLocationFlow = getLocation.invoke()
        val currentLocation = currentLocationFlow.firstOrNull()


        val markers = appstore.first().markers

        val nearestMarkers =
            currentLocation?.let { calculateNearestMarkers(it, markers) } ?: markers.take(100)

        val geofences = nearestMarkers
            .map {
                createGeofence(
                    LatLng(it.position.latitude, it.position.longitude),
                    radius,
                    Geofence.GEOFENCE_TRANSITION_ENTER,
                    it.id
                )
            }

        geofencingClient.removeGeofences(pendingIntent)
            .addOnSuccessListener {
                Log.d("GEOFENCE", "Removed all Geofences ...")
                CoroutineScope(Dispatchers.Main).launch {
                    context.datastore.updateData { it ->
                        it.copy(
                            geofences = emptyList()
                        )
                    }
                }
            }
            .addOnFailureListener { e -> Log.d("GEOFENCE", "Failed to remove all Geofences: $e") }

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
                                geofences = geofences.map {
                                    it.requestId
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

    private fun cancelGeofenceUpdateWorker() {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(GEOFENCE_WORKER_TAG) // Use the same unique name that was used to enqueue the work
        Log.d("GEOFENCE", "GeofenceUpdateWorker is cancelled")
    }

    private fun calculateNearestMarkers(
        currentLocation: LatLng,
        markers: List<MarkerModel>,
        limit: Int = 100
    ): List<MarkerModel> {
        val sortedMarkers = markers
            .map { marker ->
                // Calculate distance from current location to marker
                val distance = haversine(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    marker.position.latitude,
                    marker.position.longitude
                )
                marker to distance
            }
            .sortedBy { (_, distance) -> distance } // Sort by distance
            .take(limit) // Take the top 'limit' nearest markers
            .map { (marker, _) -> marker }

        Log.d("GEOFENCE", "Nearest markers: $sortedMarkers")

        return sortedMarkers
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0088 // Radius of the Earth in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a =
            sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(
                2
            )
        val c = 2 * asin(sqrt(a))
        return R * c
    }


}