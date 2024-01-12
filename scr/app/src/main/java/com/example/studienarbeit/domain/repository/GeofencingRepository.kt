package com.example.studienarbeit.domain.repository

import com.example.studienarbeit.domain.model.MarkerModel

interface GeofencingRepository {

    suspend fun setGeofence(markers:List<MarkerModel>)

    suspend fun removeAllGeofences()

    suspend fun updateRadius(radius:Float)

    suspend fun addMarkersAsGeofences()

}