package com.example.studienarbeit.domain.repository

import android.app.PendingIntent
import com.google.android.gms.location.Geofence
import com.google.android.gms.maps.model.LatLng

interface GeofencingRepository {

    fun setGeofence(geofences:MutableList<Geofence>)

    fun createGeofence(latLng: LatLng, radius: Float, transitionTypes: Int,id:String): Geofence

}