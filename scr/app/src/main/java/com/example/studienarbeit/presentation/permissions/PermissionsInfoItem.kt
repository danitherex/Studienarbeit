package com.example.studienarbeit.presentation.permissions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.ShareLocation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

data class PermissionsInfoItem(
    val icon: ImageVector,
    val iconColor: Color,
    val iconBackGroundColor: Color,
    val title: String,
    val description: AnnotatedString,
    val versionCode: IntRange,
) {
    companion object {
        @Composable
        fun permissionInfoItems() = listOf(

            PermissionsInfoItem(
                icon = Icons.Rounded.Notifications,
                iconColor = MaterialTheme.colorScheme.tertiary,
                iconBackGroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                title = "Notification",
                description = buildAnnotatedString {
                    append("To give you ")
                    withStyle(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append("information")
                    }
                    append(" ,whether you are next to a saved location.")
                },
                versionCode = PermissionsHandler.tiramisuOrAboveTiramisu
            ),

            PermissionsInfoItem(
                icon = Icons.Rounded.LocationOn,
                iconColor = MaterialTheme.colorScheme.tertiary,
                iconBackGroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                title = "Fine Location",
                description = buildAnnotatedString {
                    append("To get your current ")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append("location")
                    }
                    append(" for showing on the map.")
                },
                versionCode = PermissionsHandler.tiramisuOrAboveTiramisu
            ),
            PermissionsInfoItem(
                icon = Icons.Rounded.ShareLocation,
                iconColor = MaterialTheme.colorScheme.tertiary,
                iconBackGroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                title = "Background Location",
                description = buildAnnotatedString {
                    append("To get your ")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append("location")
                    }
                    append(" in the ")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append("background")
                    }
                    append(" to be able to access it, when the app is closed")
                },
                versionCode = PermissionsHandler.tiramisuOrAboveTiramisu
            ),

            )
    }
}
