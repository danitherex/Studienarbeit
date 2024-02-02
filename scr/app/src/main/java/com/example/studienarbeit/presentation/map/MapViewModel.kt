package com.example.studienarbeit.presentation.map

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.studienarbeit.domain.model.Response
import com.example.studienarbeit.domain.repository.GeofencingRepository
import com.example.studienarbeit.domain.use_case.GetLocation
import com.example.studienarbeit.domain.use_case.marker.MarkerUseCases
import com.example.studienarbeit.presentation.map.states.LocationState
import com.example.studienarbeit.presentation.map.states.MarkersState
import com.example.studienarbeit.services.GeofenceUpdateWorker
import com.example.studienarbeit.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.S)
@HiltViewModel
class MapViewModel @Inject constructor(
    private val getLocationUseCase: GetLocation,
    private val markerUseCases: MarkerUseCases,
    val geofencingHelper: GeofencingRepository,
    private val workManager: WorkManager
) : ViewModel() {


    private val _markersState: MutableStateFlow<MarkersState> =
        MutableStateFlow(MarkersState.Loading)
    val markersState = _markersState.asStateFlow()
    private val _locationState: MutableStateFlow<LocationState> =
        MutableStateFlow(LocationState.Loading)
    val locationState = _locationState.asStateFlow()
    private val _showPreviewState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showPreviewState = _showPreviewState.asStateFlow()

    //TODO: set radius as default value
    val previewRadius: MutableDoubleState = mutableDoubleStateOf(250.0)

    private var getNotesJob: Job? = null


    fun togglePreview() {
        _showPreviewState.value = !_showPreviewState.value

    }

    init {
        getNotes()
    }

    fun onEvent(event: MarkersEvents) {
        when (event) {
            is MarkersEvents.DeleteMarker -> {
                viewModelScope.launch {
                    markerUseCases.deleteMarker(event.id)
                }
            }
        }
    }

    fun handle(event: PermissionEvent) {
        when (event) {
            PermissionEvent.Granted -> {
                scheduleGeofenceUpdates()
                viewModelScope.launch {
                    getLocationUseCase.invoke().collect { location ->
                        _locationState.value = LocationState.Success(location)
                    }

                }
            }

            PermissionEvent.Revoked -> {
                _locationState.value = LocationState.RevokedPermissions
            }
        }
    }

    private fun getNotes() {
        getNotesJob?.cancel()
        getNotesJob = markerUseCases.getMarkers()
            .onEach { markers ->
                when (markers) {
                    is Response.Success -> {
                        _markersState.value = MarkersState.Success(markers.data)

                        geofencingHelper.setGeofence(markers.data)
                    }

                    is Response.Error -> {
                        Log.d("MapViewModel", "getNotes: ${markers.message}")
                    }

                    else -> {
                        Log.d("MapViewModel", "getNotes: $markers")
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun scheduleGeofenceUpdates() {
        val periodicWorkRequest = PeriodicWorkRequestBuilder<GeofenceUpdateWorker>(
            12, TimeUnit.HOURS,
            1, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            Constants.GEOFENCE_WORKER_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

}


sealed interface PermissionEvent {
    object Granted : PermissionEvent
    object Revoked : PermissionEvent
}