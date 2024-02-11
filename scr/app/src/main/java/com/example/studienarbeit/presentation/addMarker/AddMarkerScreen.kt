package com.example.studienarbeit.presentation.addMarker

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.studienarbeit.domain.model.MarkerModel
import com.example.studienarbeit.domain.model.Response
import com.example.studienarbeit.presentation.addMarker.components.TypeDropDown
import com.example.studienarbeit.presentation.map.components.ImageMarker
import com.example.studienarbeit.ui.theme.StudienarbeitTheme
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
    longitude: Double,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    val titleState = viewModel.titleState.collectAsStateWithLifecycle()
    val descriptionState = viewModel.descriptionState.collectAsStateWithLifecycle()
    val typeState = viewModel.typeState.collectAsStateWithLifecycle()

    val scope = viewModel.viewModelScope

    val innerPadding = 15.dp

    val scrollState = rememberScrollState()
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(latitude, longitude),
            15.5f
        )
    }


    Column(
        modifier = modifier
            .fillMaxSize()
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
                .verticalScroll(scrollState)
                .imePadding()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { focusManager.clearFocus() }
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = "Add Marker",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Latitude: $latitude\n" +
                        "Longitude: $longitude",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Title",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            OutlinedTextField(
                value = titleState.value,
                onValueChange = viewModel::setTitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 5.dp, end = 20.dp, bottom = 20.dp),
                label = { Text(text = "Title") },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                ),
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
                ),
            )

            Text(
                text = "Description",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )


            OutlinedTextField(
                value = descriptionState.value,
                onValueChange = viewModel::setDescription,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(start = 20.dp, top = 5.dp, end = 20.dp, bottom = 20.dp),
                label = { Text(text = "Description") },
                shape = RoundedCornerShape(10.dp),
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.clearFocus() }
                ),
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
                    color = MaterialTheme.colorScheme.onBackground,
                )
                TypeDropDown(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .align(Alignment.CenterVertically),
                    type = typeState.value,
                    onTypeChange = viewModel::setType
                )
            }

            Button(
                onClick = {
                    if (titleState.value.isEmpty() || descriptionState.value.isEmpty()) return@Button
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
                shape = AbsoluteRoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = "Save")
            }

        }

    }
}

@Preview
@Composable
fun AddMarkerScreenPreview() {
    StudienarbeitTheme(darkTheme = true) {
        AddMarkerScreen(
            viewModel = AddMarkerViewModel(null),
            navigateBack = {},
            latitude = 0.0,
            longitude = 0.0
        )
    }
}