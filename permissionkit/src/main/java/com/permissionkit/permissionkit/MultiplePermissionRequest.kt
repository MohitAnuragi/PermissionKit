package com.permissionkit.permissionkit


class MultiplePermissionRequest {
    internal var onAllGranted: (() -> Unit)? = null
    internal var onSomeGranted: ((granted: List<String>, denied: List<String>) -> Unit)? = null
    internal var onAllDenied: (() -> Unit)? = null

  
    fun allGranted(block: () -> Unit) {
        onAllGranted = block
    }


    fun someGranted(block: (granted: List<String>, denied: List<String>) -> Unit) {
        onSomeGranted = block
    }

    fun allDenied(block: () -> Unit) {
        onAllDenied = block
    }
}
