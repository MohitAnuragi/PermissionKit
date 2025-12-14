package com.permissionkit.permissionkit

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

/**
 * Internal class that handles multiple permission launching using Activity Result API.
 */
internal class MultiplePermissionLauncher(
    private val activity: ComponentActivity
) {

    private var callback: ((Map<String, Boolean>) -> Unit)? = null

    private val launcher: ActivityResultLauncher<Array<String>> =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            callback?.invoke(permissions)
        }

    fun launch(
        permissions: Array<String>,
        callback: (Map<String, Boolean>) -> Unit
    ) {
        this.callback = callback
        launcher.launch(permissions)
    }
}
