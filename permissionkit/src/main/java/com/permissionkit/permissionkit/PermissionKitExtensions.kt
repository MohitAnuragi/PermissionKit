package com.permissionkit.permissionkit

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity


fun ComponentActivity.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(intent)
}

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
