package com.example.studienarbeit.services

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.studienarbeit.domain.model.Response
import com.example.studienarbeit.domain.repository.GeofencingRepository
import com.example.studienarbeit.domain.repository.MarkerRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class GeofenceUpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val geofencingRepository: GeofencingRepository,
    private val markerRepository: MarkerRepository,
    ) : CoroutineWorker(appContext, workerParams) {

    @RequiresApi(Build.VERSION_CODES.S)
    override suspend fun doWork(): Result {


        val markersResponse = markerRepository.getMarkers().firstOrNull()
        Log.d("GeofenceUpdateWorker", "Markers: $markersResponse")

        if (markersResponse is Response.Success ) {

            // Update geofences
            geofencingRepository.setGeofence(markersResponse.data)
        }

        return Result.success()
    }

}
