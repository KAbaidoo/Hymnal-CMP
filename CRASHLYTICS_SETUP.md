# Firebase Crashlytics Setup Guide

This document describes the Firebase Crashlytics integration for the Hymnal-CMP project.

## Overview

Firebase Crashlytics is integrated into the project to:
- Track and report crashes in production
- Collect non-fatal exceptions from risky code areas
- Provide contextual information via custom keys
- Enable ProGuard/R8 mapping file upload for Android symbolication
- Enable dSYM upload for iOS symbolication (manual setup required)

## Android Setup

### ✅ Completed

1. **Dependencies Added**
   - Firebase Crashlytics SDK added to `libs.versions.toml`
   - Crashlytics Gradle plugin configured
   - Applied to `composeApp/build.gradle.kts`

2. **ProGuard/R8 Configuration**
   - Enabled minification for release builds
   - ProGuard rules configured in `composeApp/proguard-rules.pro`
   - Mapping files will be automatically uploaded to Firebase

3. **Crashlytics Integration**
   - `CrashlyticsManager` interface created for cross-platform compatibility
   - Android implementation uses Firebase Crashlytics SDK
   - Integrated with Koin DI
   - Custom keys set for app version, build type
   - Crashlytics is enabled only in release builds (disabled in debug)

4. **Exception Handling**
   - `SafeHymnRepository` wrapper created for database operations
   - Extension functions for easy exception reporting
   - Non-fatal exception reporting in critical code paths

### Build Configuration

```gradle
buildTypes {
    release {
        isMinifyEnabled = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

### Usage Example

```kotlin
// Inject CrashlyticsManager
private val crashlytics: CrashlyticsManager by inject()

// Report non-fatal exceptions
try {
    riskyOperation()
} catch (e: Exception) {
    crashlytics.log("Error in riskyOperation")
    crashlytics.setCustomKey("operation_id", operationId)
    crashlytics.recordException(e)
}

// Or use extension functions
crashlytics.safeLet(
    logMessage = "Performing risky operation",
    customKeys = mapOf("operation_id" to operationId)
) {
    riskyOperation()
}
```

## iOS Setup

### 🔧 Manual Setup Required

The iOS implementation is currently a stub. To enable full Crashlytics on iOS:

#### 1. Add Firebase Crashlytics to Xcode Project

**Option A: Using CocoaPods**

1. Create a `Podfile` in the `iosApp` directory:
```ruby
platform :ios, '14.0'

target 'iosApp' do
  use_frameworks!
  
  pod 'Firebase/Crashlytics'
  pod 'Firebase/Analytics'
end

post_install do |installer|
  installer.pods_project.targets.each do |target|
    target.build_configurations.each do |config|
      config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '14.0'
    end
  end
end
```

2. Run `pod install` in the `iosApp` directory
3. Open `iosApp.xcworkspace` (not `.xcodeproj`)

**Option B: Using Swift Package Manager**

1. In Xcode, go to File → Add Packages
2. Enter URL: `https://github.com/firebase/firebase-ios-sdk`
3. Add `FirebaseCrashlytics` and `FirebaseAnalytics` packages

#### 2. Configure dSYM Upload

1. In Xcode, select your app target
2. Go to Build Phases → + → New Run Script Phase
3. Name it "Upload dSYMs to Crashlytics"
4. Add this script:

```bash
# For CocoaPods:
"${PODS_ROOT}/FirebaseCrashlytics/run"

# For SPM:
"${BUILD_DIR%/Build/*}/SourcePackages/checkouts/firebase-ios-sdk/Crashlytics/run"
```

5. Set Input Files:
```
${DWARF_DSYM_FOLDER_PATH}/${DWARF_DSYM_FILE_NAME}/Contents/Resources/DWARF/${TARGET_NAME}
${BUILT_PRODUCTS_DIR}/${INFOPLIST_PATH}
```

#### 3. Update iOS App Initialization

Update `iosApp/iosApp/iOSApp.swift`:

```swift
import SwiftUI
import FirebaseCore
import FirebaseCrashlytics

@main
struct iOSApp: App {
    
    init() {
        FirebaseApp.configure()
        
        // Enable Crashlytics only in release builds
        #if DEBUG
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(false)
        #else
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(true)
        #endif
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

#### 4. Update Kotlin iOS Implementation

Replace the stub in `CrashlyticsManager.ios.kt` with:

```kotlin
package com.kobby.hymnal.core.crashlytics

import cocoapods.FirebaseCrashlytics.FIRCrashlytics
import platform.Foundation.NSError

class IosCrashlyticsManager : CrashlyticsManager {
    private val crashlytics = FIRCrashlytics.crashlytics()
    
    override fun recordException(throwable: Throwable) {
        val error = NSError.errorWithDomain(
            domain = throwable::class.simpleName ?: "Unknown",
            code = 0,
            userInfo = mapOf(
                "message" to (throwable.message ?: "No message"),
                "stackTrace" to (throwable.stackTraceToString())
            )
        )
        crashlytics.recordError(error)
    }
    
    override fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomValue(value, key)
    }
    
    override fun setCustomKey(key: String, value: Boolean) {
        crashlytics.setCustomValue(value, key)
    }
    
    override fun setCustomKey(key: String, value: Int) {
        crashlytics.setCustomValue(value.toLong(), key)
    }
    
    override fun setUserId(userId: String) {
        crashlytics.setUserID(userId)
    }
    
    override fun log(message: String) {
        crashlytics.log(message)
    }
}

actual fun createCrashlyticsManager(): CrashlyticsManager = IosCrashlyticsManager()
```

#### 5. Configure CocoaPods in build.gradle.kts

Add CocoaPods configuration to `composeApp/build.gradle.kts`:

```kotlin
kotlin {
    // ... existing config ...
    
    cocoapods {
        summary = "Hymnal Compose Multiplatform App"
        homepage = "https://github.com/KAbaidoo/Hymnal-CMP"
        ios.deploymentTarget = "14.0"
        
        pod("FirebaseCrashlytics") {
            version = "~> 10.0"
        }
    }
}
```

## Email Alerts Configuration

To receive email alerts for fatal crash spikes:

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Navigate to Crashlytics → Settings (gear icon)
4. Click on "Email notifications"
5. Add email: `kobbykolmes@gmail.com`
6. Configure alert settings:
   - Enable "New fatal issues"
   - Enable "Regressed issues"
   - Set velocity alert threshold (e.g., 5% increase)

## Testing Crashlytics

### Android

1. Build release variant: `./gradlew assembleRelease`
2. Install the APK on a device
3. Trigger a crash (or use `throw RuntimeException("Test Crashlytics")`)
4. Relaunch the app (crash report is sent on next launch)
5. View crash in Firebase Console after a few minutes

### iOS (After Manual Setup)

1. Build and run on a real device (not simulator)
2. Trigger a crash
3. Relaunch the app
4. View crash in Firebase Console after processing

## Custom Keys and Feature Flags

The following custom keys are automatically set:

- `app_version`: App version string
- `version_code`: Numeric version code
- `build_type`: "debug" or "release"

To add more custom keys:

```kotlin
private val crashlytics: CrashlyticsManager by inject()

// In your initialization code
crashlytics.setCustomKey("feature_flag_name", isEnabled)
crashlytics.setCustomKey("user_preference", value)
```

## Risky Code Areas Instrumented

The following areas have been instrumented with exception reporting:

1. **Database Operations** - `SafeHymnRepository` wrapper
   - All database queries and mutations
   - Search operations
   - Favorites and history management
   - Highlight operations

2. **Application Initialization** - `MainActivity`
   - Firebase initialization
   - Koin DI setup
   - Crashlytics configuration

Add more exception handling as needed:

```kotlin
// Example: Wrapping risky operations
crashlytics.safeCall(
    logMessage = "Loading hymn data",
    customKeys = mapOf("hymn_id" to hymnId)
) {
    loadHymnData(hymnId)
}
```

## ProGuard Configuration

ProGuard rules in `composeApp/proguard-rules.pro` keep:
- Firebase Crashlytics classes
- Exception metadata (line numbers, source files)
- Koin modules
- SQLDelight database classes
- Compose runtime classes

The Crashlytics plugin automatically handles mapping file upload.

## Monitoring and Analysis

Access crash reports at:
- [Firebase Console - Crashlytics](https://console.firebase.google.com/)
- Navigate to: Your Project → Crashlytics

Key metrics to monitor:
- Crash-free users percentage
- Most impacted versions
- Most common crashes
- Velocity alerts (regression detection)

## Troubleshooting

### Android: Mapping files not uploading
- Ensure `google-services.json` is in `composeApp/` directory
- Verify Firebase Crashlytics plugin is applied
- Check that minification is enabled for the build type
- Build with `--stacktrace` to see upload errors

### iOS: dSYMs not uploading
- Verify run script is in Build Phases
- Check input files are set correctly
- Build archives generate dSYMs (not debug builds)
- Run script must run after "Compile Sources" phase

### Crashes not appearing
- Crashes appear after the app is relaunched
- Can take up to 30 minutes to process
- Ensure Crashlytics is enabled in build configuration
- Check device/app has internet connectivity

## Resources

- [Firebase Crashlytics Documentation](https://firebase.google.com/docs/crashlytics)
- [Android Setup Guide](https://firebase.google.com/docs/crashlytics/get-started?platform=android)
- [iOS Setup Guide](https://firebase.google.com/docs/crashlytics/get-started?platform=ios)
- [ProGuard Configuration](https://firebase.google.com/docs/crashlytics/get-deobfuscated-reports?platform=android)
