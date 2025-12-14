package com.permissionkit.permissionkit


sealed class PermissionResult {
    object Granted : PermissionResult()
    data class Denied(
     
        val shouldShowRationale: Boolean
    ) : PermissionResult()
}
