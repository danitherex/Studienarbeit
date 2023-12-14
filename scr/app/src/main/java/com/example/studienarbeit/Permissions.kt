import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.studienarbeit.NeededPermission
import com.example.studienarbeit.PermissionDialog
import com.example.studienarbeit.getNeededPermission

@Composable
fun Permissions() {
    val activity = LocalContext.current as Activity

    val permissionDialog = remember {
        mutableStateListOf<NeededPermission>()
    }


    val backgroundPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { permission ->
            if (!permission)
                permissionDialog.add(NeededPermission.BACKGROUND_LOCATION)

        }
    )

    val multiplePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            Log.i("LogPerm", permissions.toString())
            permissions.entries.forEach { entry ->
                if (!entry.value)
                    permissionDialog.add(getNeededPermission(entry.key))
                else if (entry.key == NeededPermission.FINE_LOCATION.permission && entry.value) {
                    backgroundPermissionLauncher.launch(NeededPermission.BACKGROUND_LOCATION.permission)
                }
            }
        }
    )

    Button(
        onClick = {
            multiplePermissionLauncher.launch(
                arrayOf(
                    NeededPermission.COARSE_LOCATION.permission,
                    NeededPermission.FINE_LOCATION.permission
                )
            )
        }
    ) {
        Text(text = "Request multiple Permissions")
    }


    permissionDialog.forEach { permission ->
        PermissionDialog(
            neededPermission = permission,
            onDismiss = { permissionDialog.remove(permission) },
            onOkClick = {
                permissionDialog.remove(permission)
                multiplePermissionLauncher.launch(arrayOf(permission.permission))
            },
            onGoToAppSettingsClick = {
                permissionDialog.remove(permission)
                activity.goToAppSetting()
            },
            isPermissionDeclined = !activity.shouldShowRequestPermissionRationale(permission.permission)
        )
    }

}


fun Activity.goToAppSetting() {
    val i = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
    startActivity(i)
}