package com.permissionkit.permissionkit

import android.Manifest

/**
 * Common permission groups for convenience.
 */
object Permissions {

    /**
     * Camera permissions
     */
    val CAMERA = arrayOf(Manifest.permission.CAMERA)

    /**
     * Location permissions (both fine and coarse)
     */
    val LOCATION = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    /**
     * Storage permissions for reading/writing external storage
     */
    val STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    /**
     * Microphone/Audio recording permission
     */
    val MICROPHONE = arrayOf(Manifest.permission.RECORD_AUDIO)

    /**
     * Phone permissions
     */
    val PHONE = arrayOf(
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_PHONE_STATE
    )

    /**
     * Contacts permissions
     */
    val CONTACTS = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS
    )

    /**
     * Calendar permissions
     */
    val CALENDAR = arrayOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )

    /**
     * SMS permissions
     */
    val SMS = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS
    )
}
