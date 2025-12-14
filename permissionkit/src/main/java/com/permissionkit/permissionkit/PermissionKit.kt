package com.permissionkit.permissionkit

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

/**
 * PermissionKit - A clean, simple API for handling runtime permissions in Android.
 *
 * Usage:
 * ```kotlin
 * PermissionKit.request(this, Manifest.permission.CAMERA) {
 *     granted { openCamera() }
 *     denied { showError() }
 *     deniedPermanently { openSettings() }
 * }
 * ```
 */
object PermissionKit {

    private val launcherMap = mutableMapOf<ComponentActivity, PermissionLauncher>()
    private val multipleLauncherMap = mutableMapOf<ComponentActivity, MultiplePermissionLauncher>()

    /**
     * Initialize PermissionKit for an activity. This should be called in onCreate() before any permission requests.
     *
     * @param activity The ComponentActivity that will request permissions
     */
    fun init(activity: ComponentActivity) {
        if (!launcherMap.containsKey(activity)) {
            launcherMap[activity] = PermissionLauncher(activity)
            multipleLauncherMap[activity] = MultiplePermissionLauncher(activity)
        }
    }

    /**
     * Request a single permission with callback handling.
     *
     * @param activity The ComponentActivity requesting the permission
     * @param permission The permission to request (e.g., Manifest.permission.CAMERA)
     * @param block DSL block to configure permission callbacks
     */
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

    /**
     * Check if a permission is currently granted.
     *
     * @param activity The activity context
     * @param permission The permission to check
     * @return true if permission is granted, false otherwise
     */
    fun isPermissionGranted(activity: ComponentActivity, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if multiple permissions are granted.
     *
     * @param activity The activity context
     * @param permissions List of permissions to check
     * @return true if ALL permissions are granted, false otherwise
     */
    fun arePermissionsGranted(activity: ComponentActivity, vararg permissions: String): Boolean {
        return permissions.all { isPermissionGranted(activity, it) }
    }

    /**
     * Request multiple permissions at once.
     *
     * @param activity The ComponentActivity requesting the permissions
     * @param permissions Array of permissions to request
     * @param block DSL block to configure permission callbacks
     */
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

    /**
     * Clean up resources for an activity (call in onDestroy if needed).
     * Note: This is typically not needed as Activity Result API handles lifecycle automatically.
     *
     * @param activity The activity to clean up
     */
    fun cleanup(activity: ComponentActivity) {
        launcherMap.remove(activity)
        multipleLauncherMap.remove(activity)
    }
}
