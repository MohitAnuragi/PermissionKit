package com.permissionkit.permissionkit

/**
 * DSL builder for configuring permission request callbacks.
 */
class PermissionRequest {
    internal var onGranted: (() -> Unit)? = null
    internal var onDenied: (() -> Unit)? = null
    internal var onDeniedPermanently: (() -> Unit)? = null
    internal var onShowRationale: ((String) -> Unit)? = null

    /**
     * Called when permission is granted.
     */
    fun granted(block: () -> Unit) {
        onGranted = block
    }

    /**
     * Called when permission is denied but can be requested again.
     */
    fun denied(block: () -> Unit) {
        onDenied = block
    }

    /**
     * Called when permission is permanently denied (user selected "Don't ask again").
     * You should guide user to app settings in this case.
     */
    fun deniedPermanently(block: () -> Unit) {
        onDeniedPermanently = block
    }

    /**
     * Called before requesting permission when rationale should be shown.
     * @param permission The permission that will be requested
     */
    fun showRationale(block: (permission: String) -> Unit) {
        onShowRationale = block
    }
}