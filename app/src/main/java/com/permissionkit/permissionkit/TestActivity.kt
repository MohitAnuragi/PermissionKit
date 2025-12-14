package com.permissionkit.permissionkit

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity

/**
 * Simple test activity to verify the PermissionKit fix works correctly
 */
class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize PermissionKit FIRST - this is the fix!
        PermissionKit.init(this)

        // Now we can safely request permissions
        testPermissionRequest()
    }

    private fun testPermissionRequest() {
        PermissionKit.request(this, Manifest.permission.CAMERA) {
            granted {
                Log.d("TestActivity", "✅ Camera permission granted!")
                Toast.makeText(this@TestActivity, "Camera permission granted!", Toast.LENGTH_SHORT).show()
            }
            denied {
                Log.d("TestActivity", "❌ Camera permission denied")
                Toast.makeText(this@TestActivity, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
            deniedPermanently {
                Log.d("TestActivity", "🚫 Camera permission permanently denied")
                Toast.makeText(this@TestActivity, "Camera permission permanently denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
