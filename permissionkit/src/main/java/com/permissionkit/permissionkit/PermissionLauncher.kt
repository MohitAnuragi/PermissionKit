package com.permissionkit.permissionkit

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat

/**
 * Internal class that handles the actual permission launching using Activity Result API.
 */
internal class PermissionLauncher(
    private val activity: ComponentActivity
) {

    private var callback: ((PermissionResult) -> Unit)? = null

    private val launcher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            callback?.invoke(
                if (isGranted) {
                    PermissionResult.Granted
                } else {
                    val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        lastRequestedPermission ?: ""
                    )
                    PermissionResult.Denied(shouldShowRationale)
                }
            )
        }

    private var lastRequestedPermission: String? = null

    fun launch(
        permission: String,
        callback: (PermissionResult) -> Unit
    ) {
        this.callback = callback
        this.lastRequestedPermission = permission
        launcher.launch(permission)
    }

    fun shouldShowRationale(permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }
}
