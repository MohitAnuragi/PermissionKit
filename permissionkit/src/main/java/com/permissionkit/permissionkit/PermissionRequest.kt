package com.permissionkit.permissionkit


class PermissionRequest {
    internal var onGranted: (() -> Unit)? = null
    internal var onDenied: (() -> Unit)? = null
    internal var onDeniedPermanently: (() -> Unit)? = null
    internal var onShowRationale: ((String) -> Unit)? = null


    fun granted(block: () -> Unit) {
        onGranted = block
    }


    fun denied(block: () -> Unit) {
        onDenied = block
    }

 
    fun deniedPermanently(block: () -> Unit) {
        onDeniedPermanently = block
    }

 
    fun showRationale(block: (permission: String) -> Unit) {
        onShowRationale = block
    }
}
