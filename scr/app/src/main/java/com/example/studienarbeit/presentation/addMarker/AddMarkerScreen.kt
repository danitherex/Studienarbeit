package com.example.studienarbeit.presentation.addMarker

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.studienarbeit.domain.model.MarkerModel
import com.example.studienarbeit.domain.model.Response
import com.example.studienarbeit.presentation.map.components.ImageMarker
import com.example.studienarbeit.utils.Icons
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
fun AddMarkerScreen(
    viewModel: AddMarkerViewModel,
    navigateBack: () -> Unit,
    latitude: Double,
    longitude: Double
) {
    val titleState = viewModel.titleState.collectAsStateWithLifecycle()
    val descriptionState = viewModel.descriptionState.collectAsStateWithLifecycle()
    val typeState = viewModel.typeState.collectAsStateWithLifecycle()

    val scope = viewModel.viewModelScope

    val innerPadding = 15.dp
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(latitude, longitude),
            15.5f
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        GoogleMap(
            cameraPositionState = cameraState,
            modifier = Modifier
                .fillMaxHeight(.23f)
                .fillMaxWidth(),
            properties = MapProperties(
                isMyLocationEnabled = false,
                mapType = MapType.NORMAL,
                isTrafficEnabled = false,
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false,
                compassEnabled = false,
                zoomGesturesEnabled = false,
                indoorLevelPickerEnabled = false,
                mapToolbarEnabled = false,
                scrollGesturesEnabled = false,
                tiltGesturesEnabled = false,
                rotationGesturesEnabled = false,
                scrollGesturesEnabledDuringRotateOrZoom = false,

                ),
        ) {
            ImageMarker(
                marker = MarkerModel(
                    id = "",
                    title = "",
                    description = "",
                    position = GeoPoint(latitude, longitude),
                    userID = "",
                    type = Icons.PREVIEW.name.lowercase()
                ),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(40.dp))
                .background(color = Color.White)
        ) {
            Text(
                text = "Add Marker",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Latitude: $latitude\n" +
                        "Longitude: $longitude",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Title",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            TextField(
                value = titleState.value,
                onValueChange = viewModel::setTitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 5.dp, end = 20.dp, bottom = 20.dp),
                label = { Text(text = "Title") },
                shape = RoundedCornerShape(10.dp)
            )

            Text(
                text = "Description",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )


            TextField(
                value = descriptionState.value,
                onValueChange = viewModel::setDescription,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(start = 20.dp, top = 5.dp, end = 20.dp, bottom = 20.dp),
                label = { Text(text = "Description") },
                shape = RoundedCornerShape(10.dp),
                maxLines = 5
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .height(70.dp)
            ) {
                Text(
                    text = "Type",
                    modifier = Modifier
                        .padding(innerPadding)
                        .align(Alignment.CenterVertically),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .align(Alignment.CenterVertically),
                    shape = AbsoluteRoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(text = "Add Type")
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        viewModel.addMarker(
                            title = titleState.value,
                            description = descriptionState.value,
                            longitude = longitude,
                            latitude = latitude,
                            type = typeState.value
                        )?.collect {
                            when (it) {
                                is Response.Success -> {
                                    navigateBack()
                                }

                                is Response.Error -> {
                                    Log.e("MARKER", it.message.toString())
                                }

                                else -> {}
                            }

                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = innerPadding, end = innerPadding, bottom = innerPadding)
                    .align(Alignment.End),
                shape = AbsoluteRoundedCornerShape(10.dp)
            ) {
                Text(text = "Save")
            }

        }

    }

}

@Preview
@Composable
fun AddMarkerScreenPreview() {
    AddMarkerScreen(
        viewModel = AddMarkerViewModel(null),
        navigateBack = {},
        latitude = 0.0,
        longitude = 0.0
    )
}