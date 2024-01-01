package com.example.studienarbeit.presentation.screens.main.components

import androidx.compose.runtime.Composable
import com.example.studienarbeit.domain.model.MarkerModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun ImageMarker( marker: MarkerModel,icon: Icons, onClick: () -> Unit) {
    Marker(
        state = MarkerState(
            position = LatLng(marker.position.latitude,marker.position.longitude)
        ),
        title = marker.title,
        snippet = marker.description,
        onClick = {
            onClick()
            true
        },
        icon =  BitmapDescriptorFactory.fromResource(icon.resourceId)
    )

}