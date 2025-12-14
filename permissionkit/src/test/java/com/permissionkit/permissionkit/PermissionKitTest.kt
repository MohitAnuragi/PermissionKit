package com.permissionkit.permissionkit

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * Unit tests for PermissionKit library.
 */
@RunWith(AndroidJUnit4::class)
class PermissionKitTest {

    @Mock
    private lateinit var mockActivity: ComponentActivity

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(mockActivity.applicationContext).thenReturn(ApplicationProvider.getApplicationContext())
        `when`(mockActivity.packageName).thenReturn("com.example.permissionkit.test")
    }

    @Test
    fun `test PermissionResult sealed class states`() {
        // Test Granted state
        val granted = PermissionResult.Granted
        assertTrue(granted is PermissionResult.Granted)

        // Test Denied state with rationale
        val deniedWithRationale = PermissionResult.Denied(shouldShowRationale = true)
        assertTrue(deniedWithRationale is PermissionResult.Denied)
        assertTrue(deniedWithRationale.shouldShowRationale)

        // Test Denied state without rationale (permanently denied)
        val deniedPermanently = PermissionResult.Denied(shouldShowRationale = false)
        assertTrue(deniedPermanently is PermissionResult.Denied)
        assertFalse(deniedPermanently.shouldShowRationale)
    }

    @Test
    fun `test PermissionRequest DSL configuration`() {
        val request = PermissionRequest()
        var grantedCalled = false
        var deniedCalled = false
        var deniedPermanentlyCalled = false

        // Configure the request
        request.apply {
            granted { grantedCalled = true }
            denied { deniedCalled = true }
            deniedPermanently { deniedPermanentlyCalled = true }
        }

        // Test callbacks are set
        assertNotNull(request.onGranted)
        assertNotNull(request.onDenied)
        assertNotNull(request.onDeniedPermanently)

        // Test callbacks work
        request.onGranted?.invoke()
        assertTrue(grantedCalled)

        request.onDenied?.invoke()
        assertTrue(deniedCalled)

        request.onDeniedPermanently?.invoke()
        assertTrue(deniedPermanentlyCalled)
    }

    @Test
    fun `test MultiplePermissionRequest DSL configuration`() {
        val request = MultiplePermissionRequest()
        var allGrantedCalled = false
        var someGrantedCalled = false
        var allDeniedCalled = false

        request.apply {
            allGranted { allGrantedCalled = true }
            someGranted { _, _ -> someGrantedCalled = true }
            allDenied { allDeniedCalled = true }
        }

        // Test callbacks are set
        assertNotNull(request.onAllGranted)
        assertNotNull(request.onSomeGranted)
        assertNotNull(request.onAllDenied)

        // Test callbacks work
        request.onAllGranted?.invoke()
        assertTrue(allGrantedCalled)

        request.onSomeGranted?.invoke(listOf("camera"), listOf("location"))
        assertTrue(someGrantedCalled)

        request.onAllDenied?.invoke()
        assertTrue(allDeniedCalled)
    }

    @Test
    fun `test Permissions constants`() {
        // Test camera permissions
        assertArrayEquals(arrayOf(Manifest.permission.CAMERA), Permissions.CAMERA)

        // Test location permissions
        assertArrayEquals(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            Permissions.LOCATION
        )

        // Test storage permissions
        assertArrayEquals(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            Permissions.STORAGE
        )

        // Test microphone permissions
        assertArrayEquals(arrayOf(Manifest.permission.RECORD_AUDIO), Permissions.MICROPHONE)
    }

    @Test
    fun `test permission groups contain expected permissions`() {
        // Test that location group has both fine and coarse permissions
        assertTrue(Permissions.LOCATION.contains(Manifest.permission.ACCESS_FINE_LOCATION))
        assertTrue(Permissions.LOCATION.contains(Manifest.permission.ACCESS_COARSE_LOCATION))
        assertEquals(2, Permissions.LOCATION.size)

        // Test that storage group has read and write permissions
        assertTrue(Permissions.STORAGE.contains(Manifest.permission.READ_EXTERNAL_STORAGE))
        assertTrue(Permissions.STORAGE.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        assertEquals(2, Permissions.STORAGE.size)

        // Test that contacts group has read and write permissions
        assertTrue(Permissions.CONTACTS.contains(Manifest.permission.READ_CONTACTS))
        assertTrue(Permissions.CONTACTS.contains(Manifest.permission.WRITE_CONTACTS))
        assertEquals(2, Permissions.CONTACTS.size)
    }

    @Test
    fun `test PermissionKit initialization requirement`() {
        // Test that requesting permission without initialization throws an exception
        try {
            PermissionKit.request(mockActivity, Manifest.permission.CAMERA) {
                granted { }
                denied { }
            }
            fail("Expected IllegalStateException but none was thrown")
        } catch (e: IllegalStateException) {
            assertTrue(e.message?.contains("not initialized") ?: false)
        }
    }
}
