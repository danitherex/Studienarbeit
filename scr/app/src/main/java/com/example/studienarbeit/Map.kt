package com.example.studienarbeit

import Permissions
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun Map() {
    val context = LocalContext.current

    var location: LatandLong? = null
    location = getUserLocation(context)

    val currentLocation = LatLng(location.latitude, location.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 10f)
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = AbsoluteRoundedCornerShape(50.dp),
                onClick = {


                }) {
                Icon(Icons.Default.MyLocation, "My Location")
            }
        },
        modifier = Modifier
            .fillMaxSize()
    )
    {innerPadding ->
        Permissions()
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false
            )
        ) {
            Marker(
                state = MarkerState(position = currentLocation),
                title = "Singapore",
                snippet = "Marker in Singapore"
            )
        }
    }
}