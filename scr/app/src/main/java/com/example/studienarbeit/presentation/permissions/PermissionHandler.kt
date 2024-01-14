package com.example.studienarbeit.presentation.permissions

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

class PermissionsHandler {
    companion object {

        /**
         * The application current Sdk Version
         */
        var currentSdk = Build.VERSION.SDK_INT
            private set


        /**
         * Use this when the current Sdk lies between
         * Android 13 - 14 ..
         */
        var tiramisuOrAboveTiramisu =
            Build.VERSION_CODES.TIRAMISU..Build.VERSION_CODES.UPSIDE_DOWN_CAKE
            private set


        /**
         * This variable returns all the run time permissions
         * based on your current Sdk version of the device/
         */
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        val permissions: List<String> =
            when (currentSdk) {
                in tiramisuOrAboveTiramisu -> listOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )

                else -> emptyList()
            }

        @OptIn(ExperimentalPermissionsApi::class)
        fun handlePermission(
            permissionState: PermissionState,
            onSuccess: () -> Unit,
            shouldShowRationale: () -> Unit,
            onRejected: () -> Unit
        ) {
            when {
                permissionState.status.isGranted -> {
                    onSuccess()
                }

                permissionState.status.shouldShowRationale -> {
                    shouldShowRationale()
                }

                !permissionState.status.isGranted && !permissionState.status.shouldShowRationale -> {
                    onRejected()
                }
            }
        }

        //used to get our PermissionItems for Educatinoal UI filter by
        //using versioncode.
        fun getPermissionItems(
            permissionInfoItems: List<PermissionsInfoItem>,
        ): List<PermissionsInfoItem> {
            return when (Build.VERSION.SDK_INT) {
                // in 13 .. 14 or above
                in tiramisuOrAboveTiramisu -> permissionInfoItems
                    .filter { //Filtering by tiramisuOrAbove
                        it.versionCode == tiramisuOrAboveTiramisu
                    }
                    .toMutableList() // Converting to mutableList for adding common permission among multiple android versions


                else -> return emptyList()
            }
        }
    }
}