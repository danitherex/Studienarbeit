package com.example.studienarbeit.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.studienarbeit.domain.repository.GeofencingRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


//TODO: evtl noch Service starten, der Marker von Firebase fetcht
@AndroidEntryPoint
class BootBroadcastReciever :
    BroadcastReceiver() {

    @Inject
    lateinit var geofencingRepository: GeofencingRepository

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && intent.action != null && context != null) {
            if(intent.action.equals(Intent.ACTION_BOOT_COMPLETED)
            ) {
                CoroutineScope(Dispatchers.Main).launch {
                    geofencingRepository.addMarkersAsGeofences()
                }

            }
        }

    }

}