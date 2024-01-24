package com.example.studienarbeit.presentation.addMarker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddMarkerScreen(
    viewModel: AddMarkerViewModel,
    navigateBack: () -> Unit,
    latitude:Double,
    longitude:Double
) {

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = androidx.compose.ui.graphics.Color.Magenta)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(.9f)
                .fillMaxHeight(.8f)
                .padding(10.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(color = androidx.compose.ui.graphics.Color.White)
                .align(Alignment.BottomCenter)
        ) {
            Text(text =    "Add Marker",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                fontSize = 30.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.Black
            )

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