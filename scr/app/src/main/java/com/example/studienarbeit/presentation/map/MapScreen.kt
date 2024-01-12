package com.example.studienarbeit.presentation.map

import RadiusSlider
import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studienarbeit.R
import com.example.studienarbeit.data.repository.hasLocationPermission
import com.example.studienarbeit.presentation.map.components.MapComponent
import com.example.studienarbeit.presentation.map.components.RationaleAlert
import com.example.studienarbeit.presentation.map.states.LocationState
import com.example.studienarbeit.settings.AppSettings
import com.example.studienarbeit.settings.datastore
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    navigateTo: () -> Unit,
) {
    val context = LocalContext.current
    val viewState by viewModel.locationState.collectAsStateWithLifecycle()
    val showPreviewState = viewModel.showPreviewState.collectAsStateWithLifecycle()


    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(!context.hasLocationPermission()) {
        permissionState.launchMultiplePermissionRequest()
    }

    when {
        permissionState.allPermissionsGranted -> {
            LaunchedEffect(Unit) {
                viewModel.handle(PermissionEvent.Granted)
            }
        }

        permissionState.shouldShowRationale -> {
            RationaleAlert(onDismiss = { }) {
                permissionState.launchMultiplePermissionRequest()
            }
        }

        !permissionState.allPermissionsGranted && !permissionState.shouldShowRationale -> {
            LaunchedEffect(Unit) {
                viewModel.handle(PermissionEvent.Revoked)
            }
        }
    }

    with(viewState) {
        when (this) {
            LocationState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            LocationState.RevokedPermissions -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("We need permissions to use this app")
                    Button(
                        onClick = {
                            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        },
                        enabled = !context.hasLocationPermission()
                    ) {
                        if (context.hasLocationPermission()) CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = Color.White
                        )
                        else Text("Settings")
                    }
                }
            }

            is LocationState.Success -> {

                val currentLoc =
                    LatLng(
                        location?.latitude ?: 0.0,
                        location?.longitude ?: 0.0
                    )
                val scope = rememberCoroutineScope()
                val cameraState = rememberCameraPositionState()
                val bool = rememberSaveable { true }
                val previewRadius = viewModel.previewRadius

                val appSettings =
                    context.datastore.data.collectAsState(initial = AppSettings())


                LaunchedEffect(key1 = bool) {
                    cameraState.centerOnLocation(currentLoc)
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
                    floatingActionButton = {
                        Column {
                            if (showPreviewState.value) {
                                FloatingActionButton(
                                    containerColor = Color.Red,
                                    onClick = {
                                        viewModel.togglePreview()
                                    }) {
                                    Icon(Icons.Outlined.Close, "MyLocation floating action button")
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            FloatingActionButton(
                                containerColor = if (showPreviewState.value) Color.Green else Color.Blue,
                                onClick = {
                                    if (showPreviewState.value) {
                                        scope.launch {
                                            if (previewRadius.doubleValue != appSettings.value.radius) {
                                                viewModel.geofencingHelper.updateRadius(
                                                    previewRadius.doubleValue.toFloat()
                                                )
                                                context.datastore.updateData {
                                                    it.copy(
                                                        radius = previewRadius.doubleValue
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    viewModel.togglePreview()
                                }) {
                                Icon(
                                    if (showPreviewState.value) Icons.Outlined.Check else Icons.Outlined.Edit,
                                    "MyLocation floating action button"
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            FloatingActionButton(
                                containerColor = Color.Blue,
                                onClick = {
                                    scope.launch {
                                        cameraState.centerOnLocation(currentLoc)
                                    }
                                }) {
                                Icon(
                                    Icons.Outlined.MyLocation,
                                    "MyLocation floating action button"
                                )
                            }


                        }
                    },
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = (context.getString(R.string.app_name)))
                            },
                            actions = {
                                IconButton(onClick = {
                                    navigateTo()
                                }) {
                                    Icon(
                                        imageVector = Icons.Outlined.PersonOutline,
                                        contentDescription = "Profile"
                                    )
                                }
                            },
                            colors = topAppBarColors(containerColor = Color.Gray)
                        )
                    },
                    bottomBar = {
                        BottomAppBar {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                Button(onClick = {
                                    scope.launch {
                                        viewModel.geofencingHelper.removeAllGeofences()
                                    }
                                }) {
                                    Text("Remove all geofences")
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.CenterEnd
                    ) {

                        MapComponent(
                            currentPosition = LatLng(
                                currentLoc.latitude,
                                currentLoc.longitude
                            ),
                            innerPadding = innerPadding,
                            cameraState = cameraState,
                            markers = viewModel.markersState.collectAsState(),
                            radius = appSettings.value.radius,
                            previewRadius = previewRadius.value,
                            showPreview = showPreviewState.value
                        )
                        if (showPreviewState.value)
                            RadiusSlider(
                                radius = previewRadius
                            )
                    }
                }

            }
        }
    }

}


private suspend fun CameraPositionState.centerOnLocation(
    location: LatLng,
) = animate(
    update = CameraUpdateFactory.newLatLngZoom(
        location,
        16.3f
    ),
    durationMs = 1500
)
