package com.example.studienarbeit.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    title:String
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Delete")
            }
        },
        title = {
            Text(text = "Delete Marker")
        },
        text = {
            Text(text = "Do you want to delete your marker $title?")
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        }
    )
}