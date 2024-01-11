package com.example.studienarbeit.domain.repository

import android.app.PendingIntent
import com.example.studienarbeit.domain.model.MarkerModel
import com.google.android.gms.location.Geofence
import com.google.android.gms.maps.model.LatLng

interface GeofencingRepository {

    suspend fun setGeofence(markers:List<MarkerModel>)

    suspend fun removeAllGeofences()

}