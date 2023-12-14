package com.example.studienarbeit

import Permissions
import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

//A callback for receiving notifications from the FusedLocationProviderClient.
lateinit var locationCallback: LocationCallback

//The main entry point for interacting with the Fused Location Provider
lateinit var locationProvider: FusedLocationProviderClient

const val LOCATION_TAG = "LOCATION"


@SuppressLint("MissingPermission")
@Composable
fun getUserLocation(context: Context): LatandLong {

    // The Fused Location Provider provides access to location APIs.
    locationProvider = LocationServices.getFusedLocationProviderClient(context)

    var currentUserLocation by remember { mutableStateOf(LatandLong()) }


    DisposableEffect(key1 = locationProvider) {
        locationCallback = object : LocationCallback() {
            //1
            override fun onLocationResult(result: LocationResult) {

                locationProvider.getCurrentLocation(
                    LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
                    object : CancellationToken() {
                        override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

                        override fun isCancellationRequested() = false

                    }).addOnSuccessListener { location ->
                    location?.let {
                        val lat = location.latitude
                        val long = location.longitude
                        Log.d("Location_success", "Latitude: $lat, Longitude: $long")
                        // Update data class with location data
                        currentUserLocation = LatandLong(latitude = lat, longitude = long)
                    }
                }
                    .addOnFailureListener {
                        Log.e("Location_error", "${it.message}")
                    }


            }
        }

        locationUpdate()
        onDispose {
            stopLocationUpdate()
        }
    }
    //4
    return currentUserLocation
}

//data class to store the user Latitude and longitude
data class LatandLong(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

@SuppressLint("MissingPermission")
fun locationUpdate() {
    val locationRequest = LocationRequest.Builder(10000)
        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        .build()

    locationCallback.let {
        locationProvider.requestLocationUpdates(
            locationRequest,
            it,
            Looper.getMainLooper()
        )
    }

}

fun stopLocationUpdate() {
    try {
        //Removes all location updates for the given callback.
        val removeTask = locationProvider.removeLocationUpdates(locationCallback)
        removeTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(LOCATION_TAG, "Location Callback removed.")
            } else {
                Log.d(LOCATION_TAG, "Failed to remove Location Callback.")
            }
        }
    } catch (se: SecurityException) {
        Log.e(LOCATION_TAG, "Failed to remove Location Callback.. $se")
    }
}