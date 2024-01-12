package com.example.studienarbeit.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.location.LocationManager.MODE_CHANGED_ACTION
import android.location.LocationManager.PROVIDERS_CHANGED_ACTION
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.example.studienarbeit.domain.repository.GeofencingRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class BootBroadcastReciever :
    BroadcastReceiver() {

    @Inject
    lateinit var geofencingRepository: GeofencingRepository

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent != null && intent.action != null && context != null) {
            if (intent.action.equals(PROVIDERS_CHANGED_ACTION) && isLocationServciesAvailable(
                    context
                ) || intent.action.equals(
                    MODE_CHANGED_ACTION
                ) && isLocationModeAvailable(context)
                || intent.action.equals(Intent.ACTION_BOOT_COMPLETED) && isLocationServciesAvailable(
                    context
                )
            ) {
                CoroutineScope(Dispatchers.Main).launch {
                    geofencingRepository.addMarkersAsGeofences()
                }

            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.P)
    private fun isLocationModeAvailable(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            return locationManager.isLocationEnabled
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

    private fun isLocationServciesAvailable(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return false
    }


}