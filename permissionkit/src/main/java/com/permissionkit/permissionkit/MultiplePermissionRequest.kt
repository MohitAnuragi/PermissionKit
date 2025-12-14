package com.permissionkit.permissionkit

/**
 * DSL builder for configuring multiple permission requests.
 */
class MultiplePermissionRequest {
    internal var onAllGranted: (() -> Unit)? = null
    internal var onSomeGranted: ((granted: List<String>, denied: List<String>) -> Unit)? = null
    internal var onAllDenied: (() -> Unit)? = null

    /**
     * Called when ALL requested permissions are granted.
     */
    fun allGranted(block: () -> Unit) {
        onAllGranted = block
    }

    /**
     * Called when some permissions are granted and some are denied.
     * @param granted List of permissions that were granted
     * @param denied List of permissions that were denied
     */
    fun someGranted(block: (granted: List<String>, denied: List<String>) -> Unit) {
        onSomeGranted = block
    }

    /**
     * Called when ALL requested permissions are denied.
     */
    fun allDenied(block: () -> Unit) {
        onAllDenied = block
    }
}
