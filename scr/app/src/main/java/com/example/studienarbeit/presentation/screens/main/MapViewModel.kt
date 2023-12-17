package com.example.studienarbeit.presentation.screens.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studienarbeit.data.repository.GetLocationUseCase
import com.example.studienarbeit.domain.use_case.UseCases
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.S)
@HiltViewModel
class MapViewModel @Inject constructor(
    private val getLocationUseCase: GetLocationUseCase,
    private val useCases: UseCases
) : ViewModel() {

    private val _markersState = mutableStateOf(MarkerState())
    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Loading)
    val state: State<MarkerState> = _markersState
    val viewState = _viewState.asStateFlow()


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
                        _viewState.value = ViewState.Success(location)
                    }
                }
            }

            PermissionEvent.Revoked -> {
                _viewState.value = ViewState.RevokedPermissions
            }
        }
    }

    private fun getNotes() {
        useCases.getMarkers()
            .onEach { markers ->
                Log.d("TEST", "getNotes: $markers")
            }
            .launchIn(viewModelScope)
    }
}

sealed interface ViewState {
    object Loading : ViewState
    data class Success(val location: LatLng?) : ViewState
    object RevokedPermissions : ViewState
}

sealed interface PermissionEvent {
    object Granted : PermissionEvent
    object Revoked : PermissionEvent
}