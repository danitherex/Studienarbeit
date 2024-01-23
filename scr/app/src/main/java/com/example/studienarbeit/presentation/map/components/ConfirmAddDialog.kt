package com.example.studienarbeit.presentation.map.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.google.firebase.firestore.GeoPoint
import kotlin.math.pow
import kotlin.math.round

@Composable
fun ConfirmAddDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    location:GeoPoint
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Confirm")
            }
        },
        title = {
            Text(text = "Add Marker")
        },
        text = {
            Text(text = "Do you want to add a marker at ${roundDouble(location.latitude,3)} ${roundDouble(location.longitude,3)}?")
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        }
    )
}

fun roundDouble(number:Double,n:Int):Double{
    return round(number*10.0.pow(n))/10.0.pow(n)
}