package com.example.studienarbeit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PermissionDialog(
    neededPermission: NeededPermission,
    isPermissionDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
) {
    AlertDialog(
        title = {Text(text = neededPermission.title)},
        text={ Text(text=neededPermission.permissionTextProvider(isPermissionDeclined))},
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    if (isPermissionDeclined) onGoToAppSettingsClick() else onOkClick()
                }
            ) {
                Text(text = if (isPermissionDeclined) "Go to app setting" else "OK")
            }
        }
    )
}