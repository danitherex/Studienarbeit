package com.example.studienarbeit.presentation.screens.main.states

import com.google.android.gms.maps.model.LatLng

sealed interface LocationState {
    object Loading : LocationState
    data class Success(val location: LatLng?) : LocationState
    object RevokedPermissions : LocationState
}