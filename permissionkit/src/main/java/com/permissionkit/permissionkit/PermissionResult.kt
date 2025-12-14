package com.permissionkit.permissionkit

/**
 * Represents the result of a permission request.
 */
sealed class PermissionResult {
    /**
     * Permission was granted by the user.
     */
    object Granted : PermissionResult()

    /**
     * Permission was denied by the user.
     */
    data class Denied(
        /**
         * True if the user checked "Don't ask again" or if this is a system-level denial.
         */
        val shouldShowRationale: Boolean
    ) : PermissionResult()
}