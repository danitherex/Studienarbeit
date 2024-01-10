package com.example.studienarbeit.presentation.map

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studienarbeit.domain.model.Response
import com.example.studienarbeit.domain.repository.GeofencingRepository
import com.example.studienarbeit.domain.use_case.GetLocation
import com.example.studienarbeit.domain.use_case.UseCases
import com.example.studienarbeit.presentation.map.states.LocationState
import com.example.studienarbeit.presentation.map.states.MarkersState
import com.google.android.gms.location.Geofence
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.S)
@HiltViewModel
class MapViewModel @Inject constructor(
    private val getLocationUseCase: GetLocation,
    private val useCases: UseCases,
    private val geofencingHelper : GeofencingRepository
    ) : ViewModel() {


    private val _markersState:MutableStateFlow<MarkersState> = MutableStateFlow(MarkersState.Loading)
    val markersState = _markersState.asStateFlow()
    private val _locationState: MutableStateFlow<LocationState> = MutableStateFlow(LocationState.Loading)
    val locationState = _locationState.asStateFlow()

    private var getNotesJob: Job?=null


    init {
        getNotes()
    }

    fun onEvent(event: MarkersEvents) {
        when (event) {
            is MarkersEvents.DeleteMarker -> {
                viewModelScope.launch {
                    useCases.deleteMarker(event.id)
                }
            }
        }
    }

    fun handle(event: PermissionEvent) {
        when (event) {
            PermissionEvent.Granted -> {
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
        getNotesJob = useCases.getMarkers()
            .onEach { markers ->
                when(markers){
                    is Response.Success -> {
                        _markersState.value = MarkersState.Success(markers.data)
                        val list = mutableListOf<Geofence>().apply {
                            markers.data.forEach { markerData ->
                                add(geofencingHelper.createGeofence(
                                    LatLng(markerData.position.latitude, markerData.position.longitude),
                                    100f,
                                    Geofence.GEOFENCE_TRANSITION_ENTER ,
                                    markerData.id
                                ))
                            }
                        }

                        geofencingHelper.setGeofence(list)
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
}



sealed interface PermissionEvent {
    object Granted : PermissionEvent
    object Revoked : PermissionEvent
}