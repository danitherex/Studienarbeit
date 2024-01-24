package com.example.studienarbeit.presentation.map.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studienarbeit.domain.model.MarkerModel
import com.example.studienarbeit.presentation.map.states.BootomSheetState
import com.example.studienarbeit.presentation.map.states.MarkersState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapComponent(
    currentPosition: LatLng,
    cameraState: CameraPositionState,
    markers: State<MarkersState>,
    innerPadding: PaddingValues,
    radius: Double,
    previewRadius: Double,
    showPreview: Boolean,
) {

    var showBottomSheet by remember { mutableStateOf(false) }
    var bottomSheetState by remember { mutableStateOf(BootomSheetState("", "")) }
    var tempMarker by remember { mutableStateOf<MarkerModel?>(null) }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                //title and descritpion
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        modifier = Modifier
                            .padding(bottom = 8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        text = bottomSheetState.title
                    )
                    Text(text = bottomSheetState.description)
                }
            }
        }
        if (tempMarker != null)
            ConfirmAddDialog(onDismissRequest = { tempMarker = null }, onConfirm = {
                Log.d(
                    "Map",
                    "Add marker at ${tempMarker!!.position.latitude}, ${tempMarker!!.position.longitude}"
                )

                tempMarker = null
            },
                location = tempMarker!!.position
            )

        GoogleMap(
            modifier = Modifier
                .fillMaxSize(),
            cameraPositionState = cameraState,
            properties = MapProperties(
                isMyLocationEnabled = true,
                mapType = MapType.NORMAL,
                isTrafficEnabled = true
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false,
                compassEnabled = true
            ),
            onMapLongClick = {
                Log.d("Map", "Long click at ${it.latitude}, ${it.longitude}")
                tempMarker = MarkerModel(
                    "",
                    "",
                    "",
                    "",
                    GeoPoint(it.latitude, it.longitude),
                    Icons.PREVIEW.name.lowercase()
                )

                scope.launch {
                    cameraState.centreToNewMarker(it)
                }
            },
            onMapClick = {
                tempMarker = null
            }
        ) {
            when (markers.value) {
                is MarkersState.Success -> {
                    (markers.value as MarkersState.Success).markerModels.forEach { marker ->
                        ImageMarker(marker = marker,
                            onClick = {
                                bottomSheetState = BootomSheetState(
                                    marker.title,
                                    marker.description
                                )
                                showBottomSheet = true
                                scope.launch { sheetState.show() }
                            }
                        )
                    }
                }

                is MarkersState.Loading -> {

                }
            }
            Circle(
                center = currentPosition,
                strokeColor = Color.Black,
                radius = radius,
                strokeWidth = 20.0F,

                )
            if (showPreview)
                Circle(
                    center = currentPosition,
                    strokeColor = Color.Magenta,
                    radius = previewRadius,
                    strokeWidth = 10.0F,
                    strokePattern = listOf(Dot(), Gap(10.0f)),
                )

            tempMarker?.let {
                ImageMarker(marker = it)

            }
        }
    }
}

suspend fun CameraPositionState.centreToNewMarker(location: LatLng) = animate(
    CameraUpdateFactory.newCameraPosition(
        CameraPosition.Builder()
            .target(LatLng(location.latitude - 0.002, location.longitude))
            .zoom(16.4f)
            .bearing(0f)
            .build()
    ),
    500
)