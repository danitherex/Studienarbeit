package com.example.studienarbeit.presentation.map.components

import androidx.compose.runtime.Composable
import com.example.studienarbeit.domain.model.MarkerModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun ImageMarker( marker: MarkerModel, onClick: () -> Unit = {}) {
    var icon = Icons.entries.find { it.name==marker.type.uppercase() }
    if(icon==null){
        icon = Icons.RESTAURANT
    }
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