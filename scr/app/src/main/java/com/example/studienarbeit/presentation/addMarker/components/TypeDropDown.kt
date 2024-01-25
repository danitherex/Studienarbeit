package com.example.studienarbeit.presentation.addMarker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studienarbeit.utils.Icons
import com.google.android.gms.maps.model.BitmapDescriptorFactory

@Composable
fun TypeDropDown(
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .background(color = androidx.compose.ui.graphics.Color.White)
                .clip(shape = RoundedCornerShape(10.dp))
        ) {
            Icons.entries.forEach { icon ->
                DropdownMenuItem(
                    text = { Text(text = icon.name) },
                    onClick = {
                        expanded = false
                    },
                    trailingIcon = {
                        BitmapDescriptorFactory.fromResource(icon.resourceId)
                    }
                )

            }


        }

    }
}

@Preview
@Composable
fun TypeDropDownPreview() {
    TypeDropDown()
}