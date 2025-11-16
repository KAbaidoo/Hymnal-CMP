# Firebase Crashlytics Integration - Quick Start

This document provides a quick overview of the Firebase Crashlytics integration completed for the Hymnal-CMP project.

## 🎯 What Was Implemented

Firebase Crashlytics has been fully integrated into the Android app and documented for iOS to help reduce crashes and fix regressions quickly.

### ✅ Completed for Android

1. **SDK Integration**
   - Firebase Crashlytics 33.13.0 via BOM
   - Configured with Google Services plugin
   - Only active in release builds (disabled in debug)

2. **ProGuard/R8 Configuration**
   - Minification enabled for release builds
   - Automatic mapping file upload for stack trace symbolication
   - Custom ProGuard rules in `composeApp/proguard-rules.pro`

3. **Cross-Platform Architecture**
   - `CrashlyticsManager` interface for multiplatform support
   - Android implementation using Firebase SDK
   - Integrated with Koin dependency injection

4. **Error Reporting**
   - `SafeHymnRepository` wraps all database operations
   - Automatic exception catching and reporting
   - Custom keys for context (app version, IDs, operation details)
   - Non-fatal exception reporting throughout the app

5. **Helper Functions**
   - `safeLet()` - Execute block, return null on error
   - `safeCall()` - Suspend function variant
   - `safeExecute()` - Re-throw after logging

### 📋 iOS Setup Required

iOS integration requires manual setup (fully documented):
- See **CRASHLYTICS_SETUP.md** for complete instructions
- Stub implementation already in place
- CocoaPods or Swift Package Manager options
- dSYM upload script provided in `scripts/upload_dsyms.sh`

## 🚀 Quick Start

### For Developers

**Using Crashlytics in Your Code:**

```kotlin
// 1. Inject CrashlyticsManager
class MyViewModel(
    private val crashlytics: CrashlyticsManager
) {
    
    // 2. Report non-fatal exceptions
    fun riskyOperation() {
        try {
            performOperation()
        } catch (e: Exception) {
            crashlytics.log("Failed to perform operation")
            crashlytics.setCustomKey("operation_id", operationId)
            crashlytics.recordException(e)
        }
    }
    
    // 3. Use helper extensions
    suspend fun loadData(id: Long) {
        crashlytics.safeCall(
            logMessage = "Loading data for id: $id",
            customKeys = mapOf("data_id" to id)
        ) {
            fetchDataFromNetwork(id)
        }
    }
}
```

**Database Operations (Automatic):**

Database operations are automatically wrapped with exception reporting:

```kotlin
// Inject SafeHymnRepository for automatic error reporting
private val repository: SafeHymnRepository by inject()

// All operations report exceptions automatically
val hymn = repository.getHymnById(123L)
```

### For Build/Release Engineers

**Building Release APK:**

```bash
# Build release APK (Crashlytics active, ProGuard enabled)
./gradlew assembleRelease

# APK will be in: composeApp/build/outputs/apk/release/
# Mapping files uploaded automatically to Firebase
```

**Testing Crashlytics:**

1. Build and install release APK
2. Trigger a crash or exception
3. Restart the app (reports sent on next launch)
4. Check Firebase Console after a few minutes

### For Project Maintainers

**Configuring Email Alerts:**

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Navigate to: Crashlytics → Settings (⚙️)
4. Click "Email notifications"
5. Add: `kobbykolmes@gmail.com`
6. Enable alerts:
   - ✅ New fatal issues
   - ✅ Regressed issues  
   - ✅ Velocity alerts (5% threshold)

See `.firebase/crashlytics-alerts.json` for detailed configuration.

**iOS Setup:**

Follow the detailed guide in **CRASHLYTICS_SETUP.md**:
- Section: "iOS Setup - Manual Setup Required"
- Estimated time: 20-30 minutes
- Both CocoaPods and SPM options provided

## 📁 Key Files

### Implementation
- `CrashlyticsManager.kt` - Common interface
- `CrashlyticsManager.android.kt` - Android implementation
- `CrashlyticsManager.ios.kt` - iOS stub
- `SafeHymnRepository.kt` - Database error handling
- `CrashlyticsExtensions.kt` - Helper functions
- `CrashlyticsModule.kt` - Koin DI module

### Configuration
- `composeApp/proguard-rules.pro` - ProGuard rules
- `gradle/libs.versions.toml` - Dependencies
- `composeApp/build.gradle.kts` - Build configuration

### Documentation
- `CRASHLYTICS_SETUP.md` - Complete setup guide
- `IMPLEMENTATION_SUMMARY.md` - Implementation details
- `.firebase/crashlytics-alerts.json` - Alert configuration

### Scripts
- `scripts/upload_dsyms.sh` - iOS dSYM upload

### Tests
- `CrashlyticsManagerTest.kt` - Manager tests
- `SafeHymnRepositoryTest.kt` - Error handling tests

## 🔍 Monitoring

### Firebase Console

Access crash reports at:
- URL: https://console.firebase.google.com/
- Navigate to: Your Project → Crashlytics

**Key Metrics to Monitor:**
- Crash-free users percentage (target: 99.5%+)
- Top crashes by occurrence
- Impacted app versions
- User impact (affected users count)
- Velocity alerts (regression detection)

### Custom Keys Tracked

The following context is automatically captured with every crash:

- `app_version` - App version string
- `version_code` - Numeric version code
- `build_type` - "debug" or "release"
- `hymn_id` - For database operations
- `hymn_number` - For hymn lookups
- `category` - For category-based queries
- `search_query` - For search operations
- `operation_id` - For operation tracking

## 🛠️ Troubleshooting

### Crashes Not Appearing

**Common causes:**
- Crashes only reported after app restart
- Processing takes 5-30 minutes
- Crashlytics disabled in debug builds
- Device/app offline during restart

**Solutions:**
- Ensure release build is being tested
- Wait 30 minutes and refresh console
- Check device has internet on next launch
- Verify Firebase config files present

### Stack Traces Not Symbolicated (Android)

**Causes:**
- Mapping files not uploaded
- ProGuard not enabled
- Wrong build variant

**Solutions:**
- Verify ProGuard enabled: `isMinifyEnabled = true`
- Check mapping upload in build logs
- Rebuild with `--info` flag to see upload status

### iOS Crashes Not Reporting

**Causes:**
- CocoaPods/SPM not configured
- dSYM upload not configured
- Firebase not initialized

**Solutions:**
- Complete iOS setup per CRASHLYTICS_SETUP.md
- Verify run script in Xcode build phases
- Check Firebase initialization in iOSApp.swift

## 📊 What Gets Reported

### Automatically Reported
- Fatal crashes (app termination)
- Native crashes (NDK/Swift)
- Stack traces with line numbers
- Device info, OS version, memory state
- Custom keys at time of crash

### Manually Reported (via code)
- Non-fatal exceptions
- Caught errors in try-catch blocks
- Business logic failures
- Network errors
- Database operation failures

### Not Reported
- Debug builds (intentionally disabled)
- User data or PII
- Passwords or secrets
- Network request bodies

## 🔒 Privacy & Security

- **Only Release Builds**: Debug builds have Crashlytics disabled
- **No PII Logged**: Implementation avoids logging user data
- **ProGuard Enabled**: Code obfuscated in release builds
- **Secure Upload**: Mapping files encrypted in transit
- **CodeQL Verified**: No security vulnerabilities detected

## 📚 Additional Resources

### Documentation
- [CRASHLYTICS_SETUP.md](./CRASHLYTICS_SETUP.md) - Detailed setup guide
- [IMPLEMENTATION_SUMMARY.md](./IMPLEMENTATION_SUMMARY.md) - Technical details
- [Firebase Crashlytics Docs](https://firebase.google.com/docs/crashlytics)

### Getting Help
- Check troubleshooting section above
- Review Firebase Crashlytics documentation
- Check Gradle build logs for upload errors
- Verify Firebase config files are correct

## ✅ Checklist for Production

- [ ] Email alerts configured in Firebase Console
- [ ] iOS setup completed (if deploying to iOS)
- [ ] Test crashes sent and received successfully
- [ ] Stack traces properly symbolicated
- [ ] Team has access to Firebase Console
- [ ] Monitoring dashboard set up
- [ ] Incident response process defined

## 🎉 Summary

Firebase Crashlytics is fully integrated for Android and documented for iOS. The implementation:

- ✅ Reduces crashes through better visibility
- ✅ Helps fix regressions quickly with alerts
- ✅ Provides rich context for debugging
- ✅ Only active in release builds
- ✅ Automatically reports database errors
- ✅ Follows security best practices
- ✅ Includes comprehensive documentation

**You're all set!** Build a release APK, configure email alerts, and start monitoring crashes in the Firebase Console.

For detailed setup instructions, see [CRASHLYTICS_SETUP.md](./CRASHLYTICS_SETUP.md).
