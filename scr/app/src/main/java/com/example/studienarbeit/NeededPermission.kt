package com.example.studienarbeit

import android.Manifest

enum class NeededPermission(
    val permission: String,
    val title: String,
    val description: String,
    val permanentlyDeniedDescription: String
) {
    COARSE_LOCATION(
        permission = Manifest.permission.ACCESS_COARSE_LOCATION,
        title = "Approximate Location Permission",
        description = "This permission is needed to get your approximate location. Please grant the permission.",
        permanentlyDeniedDescription = "This permission is needed to get your approximate location. Please grant the permission in app settings.",
    ),
    FINE_LOCATION(
        permission = Manifest.permission.ACCESS_FINE_LOCATION,
        title = "Precise Location Permission",
        description = "This permission is needed to get your precise location. Please grant the permission.",
        permanentlyDeniedDescription = "This permission is needed to get your precise location. Please grant the permission in app settings.",
    ),
    BACKGROUND_LOCATION(
        permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        title = "Background Location Permission",
        description = "This permission is needed to get your location, when the App is running in the background. Please grant the permission.",
        permanentlyDeniedDescription = "This permission is needed to get your location, when the App is running in the background. Please grant the permission in app settings.",
    );

    fun permissionTextProvider(isPermanentDenied: Boolean): String {
        return if (isPermanentDenied) this.permanentlyDeniedDescription else this.description
    }
}

fun getNeededPermission(permission: String): NeededPermission {
    return NeededPermission.entries.find { it.permission == permission } ?: throw IllegalArgumentException("Permission $permission is not supported")
}