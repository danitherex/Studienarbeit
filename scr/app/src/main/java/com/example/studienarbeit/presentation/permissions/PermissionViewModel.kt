package com.example.studienarbeit.presentation.permissions

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.studienarbeit.presentation.permissions.states.PermissionState

class PermissionViewModel : ViewModel() {
    val granted: MutableState<Boolean> = mutableStateOf(false)

    val visiblePermissionDialogQueue = mutableStateListOf<PermissionState>()

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permissionState: PermissionState,
        isGranted: Boolean,
    ) {
        if (!isGranted && !visiblePermissionDialogQueue.any {
                it.permission == permissionState.permission
            }) {
            visiblePermissionDialogQueue.add(permissionState)
        }else if(isGranted){
            val stateToRemove = visiblePermissionDialogQueue.find { it.permission == permissionState.permission }
            stateToRemove?.let { visiblePermissionDialogQueue.remove(it) }

        }
    }

}