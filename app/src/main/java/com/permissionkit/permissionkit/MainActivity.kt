package com.permissionkit.permissionkit

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.permissionkit.permissionkit.ui.theme.PermissionKitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize PermissionKit before any permission requests
        PermissionKit.init(this)

        enableEdgeToEdge()
        setContent {
            PermissionKitTheme {
                PermissionKitDemo()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PermissionKitDemo() {
        val context = LocalContext.current
        var logMessages by remember { mutableStateOf(listOf<String>()) }

        fun log(message: String) {
            logMessages = logMessages + message
            Log.d("PermissionKit", message)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "PermissionKit Demo",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "🎯 Single Permission Examples",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Button(
                        onClick = {
                            PermissionKit.request(this@MainActivity, Manifest.permission.CAMERA) {
                                granted {
                                    log("✅ Camera permission GRANTED")
                                }
                                denied {
                                    log("❌ Camera permission DENIED")
                                }
                                deniedPermanently {
                                    log("🚫 Camera permission PERMANENTLY DENIED - Open Settings")
                                }
                                showRationale { permission ->
                                    log("ℹ️ Should show rationale for $permission")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Request Camera Permission")
                    }
                }

                item {
                    Button(
                        onClick = {
                            PermissionKit.request(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) {
                                granted {
                                    log("✅ Location permission GRANTED")
                                }
                                denied {
                                    log("❌ Location permission DENIED")
                                }
                                deniedPermanently {
                                    log("🚫 Location permission PERMANENTLY DENIED")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Request Location Permission")
                    }
                }

                item {
                    Divider()
                    Text(
                        "🎯 Multiple Permissions Examples",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    Button(
                        onClick = {
                            PermissionKit.requestMultiple(
                                this@MainActivity,
                                Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO
                            ) {
                                allGranted {
                                    log("✅ Camera + Microphone ALL GRANTED")
                                }
                                someGranted { granted, denied ->
                                    log("⚠️ Some granted: $granted, denied: $denied")
                                }
                                allDenied {
                                    log("❌ Camera + Microphone ALL DENIED")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Request Camera + Microphone")
                    }
                }

                item {
                    Button(
                        onClick = {
                            PermissionKit.requestMultiple(
                                this@MainActivity,
                                *Permissions.LOCATION
                            ) {
                                allGranted {
                                    log("✅ ALL Location permissions GRANTED")
                                }
                                someGranted { granted, denied ->
                                    log("⚠️ Location - Granted: ${granted.size}, Denied: ${denied.size}")
                                }
                                allDenied {
                                    log("❌ ALL Location permissions DENIED")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Request Location Group")
                    }
                }

                item {
                    Divider()
                    Text(
                        "🎯 Permission Checks",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    Button(
                        onClick = {
                            val hasCamera = PermissionKit.isPermissionGranted(
                                this@MainActivity,
                                Manifest.permission.CAMERA
                            )
                            log("📱 Camera permission status: ${if (hasCamera) "GRANTED" else "NOT GRANTED"}")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Check Camera Permission")
                    }
                }

                item {
                    Button(
                        onClick = {
                            val hasAll = PermissionKit.arePermissionsGranted(
                                this@MainActivity,
                                Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            log("📱 All permissions status: ${if (hasAll) "ALL GRANTED" else "NOT ALL GRANTED"}")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Check Multiple Permissions")
                    }
                }

                item {
                    Divider()
                    Text(
                        "📋 Log Messages",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(logMessages.size) { index ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = logMessages[logMessages.size - 1 - index], // Reverse order (newest first)
                            modifier = Modifier.padding(12.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

