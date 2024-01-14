package com.example.studienarbeit.domain.repository

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun requestLocationUpdates(interval: Long): Flow<LatLng?>
}