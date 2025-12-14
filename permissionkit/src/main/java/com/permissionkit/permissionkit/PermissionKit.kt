package com.permissionkit.permissionkit

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat


object PermissionKit {

    private val launcherMap = mutableMapOf<ComponentActivity, PermissionLauncher>()
    private val multipleLauncherMap = mutableMapOf<ComponentActivity, MultiplePermissionLauncher>()

  
    fun init(activity: ComponentActivity) {
        if (!launcherMap.containsKey(activity)) {
            launcherMap[activity] = PermissionLauncher(activity)
            multipleLauncherMap[activity] = MultiplePermissionLauncher(activity)
        }
    }


    fun request(
        activity: ComponentActivity,
        permission: String,
        block: PermissionRequest.() -> Unit
    ) {
        val request = PermissionRequest().apply(block)

        // Check if permission is already granted
        if (isPermissionGranted(activity, permission)) {
            request.onGranted?.invoke()
            return
        }

        val launcher = launcherMap[activity]
            ?: throw IllegalStateException("PermissionKit not initialized for this activity. Call PermissionKit.init(this) in onCreate()")

        // Check if we should show rationale
        if (launcher.shouldShowRationale(permission)) {
            request.onShowRationale?.invoke(permission)
            // Still launch the permission request after rationale
        }

        launcher.launch(permission) { result ->
            when (result) {
                is PermissionResult.Granted -> request.onGranted?.invoke()
                is PermissionResult.Denied -> {
                    if (result.shouldShowRationale) {
                        request.onDenied?.invoke()
                    } else {
                        request.onDeniedPermanently?.invoke() ?: request.onDenied?.invoke()
                    }
                }
            }
        }
    }

  
    fun isPermissionGranted(activity: ComponentActivity, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }


    fun arePermissionsGranted(activity: ComponentActivity, vararg permissions: String): Boolean {
        return permissions.all { isPermissionGranted(activity, it) }
    }


    fun requestMultiple(
        activity: ComponentActivity,
        vararg permissions: String,
        block: MultiplePermissionRequest.() -> Unit
    ) {
        val request = MultiplePermissionRequest().apply(block)

        // Check which permissions are already granted
        val alreadyGranted = permissions.filter { isPermissionGranted(activity, it) }
        val needToRequest = permissions.filter { !isPermissionGranted(activity, it) }

        if (needToRequest.isEmpty()) {
            // All permissions already granted
            request.onAllGranted?.invoke()
            return
        }

        val launcher = multipleLauncherMap[activity]
            ?: throw IllegalStateException("PermissionKit not initialized for this activity. Call PermissionKit.init(this) in onCreate()")

        launcher.launch(needToRequest.toTypedArray()) { results ->
            val granted = mutableListOf<String>()
            val denied = mutableListOf<String>()

            // Add already granted permissions
            granted.addAll(alreadyGranted)

            // Process results
            results.forEach { (permission, isGranted) ->
                if (isGranted) {
                    granted.add(permission)
                } else {
                    denied.add(permission)
                }
            }

            when {
                denied.isEmpty() -> request.onAllGranted?.invoke()
                granted.isEmpty() -> request.onAllDenied?.invoke()
                else -> request.onSomeGranted?.invoke(granted, denied)
            }
        }
    }


    fun cleanup(activity: ComponentActivity) {
        launcherMap.remove(activity)
        multipleLauncherMap.remove(activity)
    }
}
