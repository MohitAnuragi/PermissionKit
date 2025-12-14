# PermissionKit 🛡️

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-blue.svg)](https://kotlinlang.org/)

**PermissionKit** is a lightweight Android library that simplifies runtime permission handling using the **Activity Result API** and a **Kotlin DSL**.
It removes legacy boilerplate (`onRequestPermissionsResult`) and provides a **clear, lifecycle-safe, and readable API** that works with both **XML and Jetpack Compose** apps.


## 🎯 Why PermissionKit?

**Before PermissionKit** (traditional approach):
```kotlin
// Lots of boilerplate code 😵
ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE)

override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
) {
    if (requestCode == REQUEST_CODE) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
        } else {
            // Permission denied
        }
    }
}
```

**With PermissionKit** (clean & modern):
```kotlin
// Clean, readable, modern 🎉
PermissionKit.request(this, Manifest.permission.CAMERA) {
    granted { openCamera() }
    denied { showError("Camera permission required") }
    deniedPermanently { openAppSettings() }
}
```
✔ Less code
✔ No deprecated APIs
✔ Lifecycle-safe
✔ Self-documenting

## 🚀 Features

- ✅ **Zero Boilerplate** - No need for `onRequestPermissionsResult`
- ✅ **Modern Activity Result API** - Uses the latest Android APIs
- ✅ **Kotlin DSL** - Beautiful, readable syntax
- ✅ **Lifecycle Safe** - Automatic lifecycle management
- ✅ **Multiple Permissions** - Handle multiple permissions at once
- ✅ **Rationale Support** - Built-in rationale handling
- ✅ **Permanent Denial Detection** - Detect "Don't ask again"
- ✅ **Predefined Groups** - Common permission groups included
- ✅ **Extension Functions** - Convenient helpers for common scenarios

## 📦 Installation

Add to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":permissionkit"))
}
```
# JitPack (recommended)

step 1 : Add it in your settings.gradle.kts at the end of repositories:

```
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("https://jitpack.io") }
		}
	}
```
step 2 : Add the dependency
```
dependencies {
	        implementation("com.github.MohitAnuragi:PermissionKit:v1.0.0")
	}
```

## 🎮 Quick Start

### Initialization (Required)

Add this to your Activity's `onCreate()` method **before** requesting any permissions:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize PermissionKit (required)
        PermissionKit.init(this)
        
        setContent {
            // Your UI code
        }
    }
}
```

### Single Permission

```kotlin
class MainActivity : ComponentActivity() {
    
    fun requestCameraPermission() {
        PermissionKit.request(this, Manifest.permission.CAMERA) {
            granted {
                // Permission granted - open camera
                openCamera()
            }
            denied {
                // Permission denied - show explanation
                showToast("Camera permission is required")
            }
            deniedPermanently {
                // User selected "Don't ask again"
                showSettingsDialog()
            }
        }
    }
}
```

### Multiple Permissions

```kotlin
PermissionKit.requestMultiple(
    this,
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO
) {
    allGranted {
        // All permissions granted
        startVideoRecording()
    }
    someGranted { granted, denied ->
        // Some permissions granted
        handlePartialPermissions(granted, denied)
    }
    allDenied {
        // All permissions denied
        showPermissionError()
    }
}
```

### Permission Groups

Use predefined permission groups for convenience:

```kotlin
// Location permissions
PermissionKit.requestMultiple(this, *Permissions.LOCATION) {
    allGranted { startLocationTracking() }
    allDenied { showLocationError() }
}

// Storage permissions
PermissionKit.requestMultiple(this, *Permissions.STORAGE) {
    allGranted { saveFile() }
    allDenied { showStorageError() }
}
```

## 🛠️ Advanced Usage

### Check Permissions

```kotlin
// Check single permission
val hasCamera = PermissionKit.isPermissionGranted(this, Manifest.permission.CAMERA)

// Check multiple permissions
val hasAllLocationPerms = PermissionKit.arePermissionsGranted(
    this,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)
```

### Extension Functions

For even cleaner code, use the provided extensions:

```kotlin
// Request camera with default behavior
this.requestCameraPermission(
    onGranted = { openCamera() },
    onDenied = { showError() }
    // onPermanentlyDenied defaults to opening app settings
)

// Request location with default behavior  
this.requestLocationPermission(
    onGranted = { startTracking() }
)
```

### Rationale Handling

```kotlin
PermissionKit.request(this, Manifest.permission.CAMERA) {
    showRationale { permission ->
        // Show explanation before requesting permission
        showRationaleDialog("Camera access is needed to take photos")
    }
    granted { openCamera() }
    denied { showError() }
}
```

## 📱 Available Permission Groups

PermissionKit includes predefined groups for common permissions:

```kotlin
Permissions.CAMERA          // Camera permission
Permissions.LOCATION        // Fine & coarse location
Permissions.STORAGE         // Read & write external storage
Permissions.MICROPHONE      // Audio recording

```

## 🎯 Best Practices

### 1. Initialize Early
Always initialize PermissionKit in `onCreate()` before any permission requests:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    PermissionKit.init(this) // Required!
    // ... rest of your onCreate code
}
```

### 2. Handle All States
Always handle all permission states for the best user experience:

```kotlin
PermissionKit.request(this, Manifest.permission.CAMERA) {
    granted {
        // Happy path - permission granted
    }
    denied {
        // Show explanation and allow retry
    }
    deniedPermanently {
        // Guide user to settings
        openAppSettings()
    }
}
```

### 3. Show Rationale
Explain why you need the permission:

```kotlin
PermissionKit.request(this, Manifest.permission.CAMERA) {
    showRationale { permission ->
        AlertDialog.Builder(this@MainActivity)
            .setTitle("Camera Permission")
            .setMessage("This app needs camera access to take photos")
            .setPositiveButton("OK", null)
            .show()
    }
    granted { openCamera() }
}
```

### 4. Group Related Permissions
Request related permissions together:

```kotlin
// Good - related permissions together
PermissionKit.requestMultiple(
    this,
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO
) {
    allGranted { startVideoRecording() }
}
```

## 🏗️ Architecture

PermissionKit is built with modern Android development practices:

- **Activity Result API** - Uses the modern permission request API
- **Sealed Classes** - Type-safe result handling
- **Kotlin DSL** - Builder pattern with lambdas
- **Lifecycle Aware** - Automatic cleanup and lifecycle management
- **Extension Functions** - Kotlin idiomatic patterns

### Core Components

1. **PermissionKit** - Main API entry point
2. **PermissionResult** - Sealed class representing permission states
3. **PermissionRequest** - DSL builder for single permissions
4. **MultiplePermissionRequest** - DSL builder for multiple permissions
5. **PermissionLauncher** - Internal Activity Result API wrapper

## 🔧 Requirements

- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 36
- **Kotlin**: 1.9.0+
- **AndroidX**: Yes
- **UI**: XML / Jetpack Compose

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Contact 
Email : anuragimohit468@gmail.com
