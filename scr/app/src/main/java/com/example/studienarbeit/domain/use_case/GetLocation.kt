package com.example.studienarbeit.domain.use_case

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.studienarbeit.domain.repository.LocationRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocation @Inject constructor(
    private val locationRepository: LocationRepository
) {
    @RequiresApi(Build.VERSION_CODES.S)
    operator fun invoke(): Flow<LatLng?> = locationRepository.requestLocationUpdates(10000L)

}