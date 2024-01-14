package com.example.studienarbeit.presentation.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studienarbeit.R
import com.example.studienarbeit.presentation.permissions.components.BackgroundLocationPermissionTextProvider
import com.example.studienarbeit.presentation.permissions.components.CoarseLocationPermissionTextProvider
import com.example.studienarbeit.presentation.permissions.components.FineLocationPermissionTextProvider
import com.example.studienarbeit.presentation.permissions.components.NotificationPermissionTextProvider
import com.example.studienarbeit.presentation.permissions.components.PermissionDialog
import com.example.studienarbeit.presentation.permissions.states.PermissionState
import com.example.studienarbeit.ui.theme.StudienarbeitTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PermissionScreen(viewModel: PermissionViewModel) {
    PermissionScreenBody {
        PermissionScreenContent(viewModel)
    }
}

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val poppinsFont = GoogleFont("Poppins")
val poppinsFontFamily = FontFamily(
    Font(googleFont = poppinsFont, fontProvider = provider)
)


@Composable
private fun PermissionScreenBody(
    content: @Composable (PaddingValues) -> Unit,
) {
    StudienarbeitTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.surface) {
            content(it)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun PermissionScreenContent(
    viewModel: PermissionViewModel,
) {
    val context = LocalContext.current

    val permissionsGranted = viewModel.granted
    val dialogQueue = viewModel.visiblePermissionDialogQueue

    val permissionsRequiredItems = PermissionsHandler.getPermissionItems(
        PermissionsInfoItem.permissionInfoItems()
    )
    val permissionState = rememberMultiplePermissionsState(
        permissions = PermissionsHandler
            .permissions //list of permissions that we need to request
    )
    val backgroundPermissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION)

    if (permissionState.allPermissionsGranted) {
        LaunchedEffect(Unit) {
            backgroundPermissionState.launchPermissionRequest()
        }
        if (backgroundPermissionState.status.isGranted) {
            permissionsGranted.value = true
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.4f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.Key,
                    tint = MaterialTheme.colorScheme.tertiary,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                )
                //If the Android version is unsupported like below Nought(7) or After UpSideDownCape(14)
                if (permissionsRequiredItems.isEmpty()) {
                    Text(
                        text = "Sorry this application won't work on your device.",
                        modifier = Modifier.padding(horizontal = 40.dp),
                        textAlign = TextAlign.Center,
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        letterSpacing = 0.24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    Text(
                        text = "we need your permission to access some of our features to work properly",
                        modifier = Modifier.padding(horizontal = 40.dp),
                        textAlign = TextAlign.Center,
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        letterSpacing = 0.24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 25.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            items(permissionsRequiredItems.size) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = permissionsRequiredItems[index].icon,
                        contentDescription = null,
                        tint = permissionsRequiredItems[index].iconColor,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(permissionsRequiredItems[index].iconBackGroundColor)
                    )
                    Column(
                        modifier = Modifier.padding(start = 10.dp)
                    ) {
                        Text(
                            text = permissionsRequiredItems[index].title,
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            letterSpacing = 0.15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = permissionsRequiredItems[index].description,
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            letterSpacing = 0.5.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        dialogQueue
            .reversed()
            .forEach { permState ->
                PermissionDialog(
                    permissionTextProvider = when (permState.permission) {
                        Manifest.permission.POST_NOTIFICATIONS -> {
                            NotificationPermissionTextProvider()
                        }

                        Manifest.permission.ACCESS_COARSE_LOCATION -> {
                            CoarseLocationPermissionTextProvider()
                        }

                        Manifest.permission.ACCESS_FINE_LOCATION -> {
                            FineLocationPermissionTextProvider()
                        }

                        Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                            BackgroundLocationPermissionTextProvider()
                        }

                        else -> return@forEach
                    },
                    isPermanentlyDeclined = permState.isPermanentlyDeclined,
                    onDismiss = viewModel::dismissDialog,
                    onOkClick = {
                        viewModel.dismissDialog()
                        permissionState.launchMultiplePermissionRequest()
                        performPermissions(permissionState, viewModel::onPermissionResult)
                    },
                    onGoToAppSettingsClick = { openAppSettings(context) }
                )
            }


        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(
                onClick = {
                    if (permissionState.allPermissionsGranted) {
                        openAppSettings(context)
                    }else{
                        permissionState.launchMultiplePermissionRequest()
                        performPermissions(permissionState, viewModel::onPermissionResult)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(.6f)
                    .height(50.dp)
            ) {
                if (permissionsRequiredItems.isEmpty()) {
                    Text(
                        text = "Exit",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = if (permissionState.allPermissionsGranted) "Open AppSettings" else "Grant Permissions",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun performPermissions(
    permissionsState: MultiplePermissionsState,
    onPermissionResult: (permissionState: PermissionState, isGranted: Boolean) -> Unit,
) {
    permissionsState
        .permissions
        .forEach { permissionState ->
            PermissionsHandler
                .handlePermission(
                    permissionState,
                    onSuccess = {
                        onPermissionResult(
                            PermissionState(
                                permission = permissionState.permission,
                                isPermanentlyDeclined = false
                            ),
                            true,
                        )
                    },
                    onRejected = {
                        onPermissionResult(
                            PermissionState(
                                permission = permissionState.permission,
                                isPermanentlyDeclined = false
                            ),
                            false,
                        )

                    },
                    shouldShowRationale = {
                        onPermissionResult(
                            PermissionState(
                                permission = permissionState.permission,
                                isPermanentlyDeclined = true
                            ),
                            false,
                        )
                    }
                )
        }

}

fun openAppSettings(context: Context) {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).also {
        context.startActivity(it)
    }
}

