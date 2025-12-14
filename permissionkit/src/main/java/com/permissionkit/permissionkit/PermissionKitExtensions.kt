package com.permissionkit.permissionkit

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity

/**
 * Extension functions for PermissionKit to handle common scenarios.
 */

/**
 * Opens the app settings page where user can manually grant permissions.
 */
fun ComponentActivity.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(intent)
}

/**
 * Request camera permission with sensible defaults.
 */
fun ComponentActivity.requestCameraPermission(
    onGranted: () -> Unit,
    onDenied: (() -> Unit)? = null,
    onPermanentlyDenied: (() -> Unit)? = null
) {
    // Ensure PermissionKit is initialized
    PermissionKit.init(this)

    PermissionKit.request(this, android.Manifest.permission.CAMERA) {
        granted { onGranted() }
        denied { onDenied?.invoke() }
        deniedPermanently {
            onPermanentlyDenied?.invoke() ?: openAppSettings()
        }
    }
}

/**
 * Request location permissions with sensible defaults.
 */
fun ComponentActivity.requestLocationPermission(
    onGranted: () -> Unit,
    onDenied: (() -> Unit)? = null,
    onPermanentlyDenied: (() -> Unit)? = null
) {
    // Ensure PermissionKit is initialized
    PermissionKit.init(this)

    PermissionKit.request(this, android.Manifest.permission.ACCESS_FINE_LOCATION) {
        granted { onGranted() }
        denied { onDenied?.invoke() }
        deniedPermanently {
            onPermanentlyDenied?.invoke() ?: openAppSettings()
        }
    }
}

/**
 * Request storage permissions with sensible defaults.
 */
fun ComponentActivity.requestStoragePermission(
    onGranted: () -> Unit,
    onDenied: (() -> Unit)? = null,
    onPermanentlyDenied: (() -> Unit)? = null
) {
    // Ensure PermissionKit is initialized
    PermissionKit.init(this)

    PermissionKit.requestMultiple(this, *Permissions.STORAGE) {
        allGranted { onGranted() }
        someGranted { granted, denied ->
            // If we got at least read permission, consider it success
            if (granted.contains(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                onGranted()
            } else {
                onDenied?.invoke()
            }
        }
        allDenied {
            onPermanentlyDenied?.invoke() ?: openAppSettings()
        }
    }
}
