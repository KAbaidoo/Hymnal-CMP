# Firebase Performance Monitoring

This document describes the Firebase Performance Monitoring implementation in the Hymnal CMP app.

## Overview

Firebase Performance Monitoring has been integrated to track and analyze app performance, including:
- **Automatic traces**: App startup and screen rendering (provided by Firebase)
- **Custom traces**: Database operations, screen loading, and other heavy tasks

## Architecture

The implementation follows the same pattern as the existing Crashlytics integration:

```
commonMain/
  ├── core/performance/
  │   ├── PerformanceManager.kt       # Common interface
  │   └── PerformanceExtensions.kt    # Helper functions
  └── di/PerformanceModule.kt         # Koin DI module

androidMain/
  └── core/performance/
      └── PerformanceManager.android.kt  # Android implementation using Firebase SDK

iosMain/
  └── core/performance/
      └── PerformanceManager.ios.kt      # iOS stub implementation
```

## Components

### PerformanceManager Interface

```kotlin
interface PerformanceManager {
    fun startTrace(traceName: String): Trace
    fun putAttribute(attribute: String, value: String)
}

interface Trace {
    fun stop()
    fun putMetric(metricName: String, value: Long)
    fun incrementMetric(metricName: String)
    fun putAttribute(attribute: String, value: String)
}
```

### Platform Implementations

#### Android
- Uses `FirebasePerformance` SDK
- Only enabled in **release builds** to avoid debug overhead
- Automatically collects automatic traces (app start, screen rendering)

#### iOS
- Stub implementation with console logging
- Ready for full integration with Firebase Performance CocoaPod

## Custom Traces

The following custom traces have been implemented:

### 1. Database Operations

#### Database Initialization
- **Trace**: `db_initialization`
- **Location**: `DatabaseHelper.android.kt`
- **Attributes**: 
  - `action`: "copy_from_assets" or "already_initialized"
- **Metrics**:
  - `database_size_bytes`: Size of the database file

#### Get Hymn by ID
- **Trace**: `db_get_hymn_by_id`
- **Location**: `HymnRepository.kt`
- **Attributes**:
  - `hymn_id`: The ID of the hymn being fetched

#### Search Hymns
- **Trace**: `db_search_hymns`
- **Location**: `HymnRepository.kt`
- **Attributes**:
  - `query_length`: Length of the search query
- **Metrics**:
  - `results_count`: Number of search results

#### Add to Favorites
- **Trace**: `db_add_to_favorites`
- **Location**: `HymnRepository.kt`
- **Attributes**:
  - `hymn_id`: The ID of the hymn being favorited

#### Add to History
- **Trace**: `db_add_to_history`
- **Location**: `HymnRepository.kt`
- **Attributes**:
  - `hymn_id`: The ID of the hymn being added to history

### 2. Screen Rendering

#### Start Screen
- **Trace**: `screen_start_render`
- **Location**: `StartScreen.kt`
- **Attributes**:
  - `screen_name`: "StartScreen"

#### Start Screen - Load Hymn
- **Trace**: `start_screen_load_hymn`
- **Location**: `StartScreen.kt`
- **Attributes**:
  - `hymn_loaded`: "true" or "false"
  - `error`: Error message if hymn failed to load

## Usage Examples

### Basic Trace

```kotlin
val performanceManager: PerformanceManager by inject()

val trace = performanceManager.startTrace("my_custom_trace")
try {
    // Your code here
    trace.putAttribute("status", "success")
} catch (e: Exception) {
    trace.putAttribute("error", e.message ?: "unknown")
} finally {
    trace.stop()
}
```

### Using Extension Functions

```kotlin
// Synchronous code
performanceManager.trace("sync_operation") { trace ->
    // Your code
    trace.putMetric("items_processed", 100)
}

// Asynchronous code
performanceManager.traceSuspend("async_operation") { trace ->
    val result = repository.fetchData()
    trace.putMetric("items_count", result.size.toLong())
    result
}
```

### Adding Metrics and Attributes

```kotlin
val trace = performanceManager.startTrace("process_data")
trace.putAttribute("data_type", "hymns")
trace.putMetric("items_count", 42L)
trace.incrementMetric("errors")  // Increment by 1
trace.stop()
```

## Setup and Configuration

### Dependencies

The following dependencies have been added to the project:

**gradle/libs.versions.toml**:
```toml
firebase-perf-plugin = "1.4.2"
firebase-perf = { module = "com.google.firebase:firebase-perf-ktx" }
```

**composeApp/build.gradle.kts**:
```kotlin
plugins {
    alias(libs.plugins.firebase.perf)
}

dependencies {
    implementation(libs.firebase.perf)
}
```

### Initialization

Performance monitoring is initialized in `MainActivity.onCreate()`:

```kotlin
startKoin {
    modules(/* ... */, performanceModule)
}

// Set global attributes
val performance: PerformanceManager by inject()
performance.putAttribute("app_version", BuildKonfig.VERSION_NAME)
performance.putAttribute("build_type", if (BuildConfig.DEBUG) "debug" else "release")
```

## Automatic Traces

Firebase Performance automatically tracks:
1. **App start time**: From app launch to first frame rendered
2. **Screen rendering**: Time to render each screen/activity
3. **Network requests**: HTTP/HTTPS request performance (when configured)

These automatic traces are collected by the Firebase Performance SDK without any additional code.

## Viewing Performance Data

1. Open the [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Navigate to **Performance** in the left menu
4. View:
   - **Dashboard**: Overall performance metrics
   - **Traces**: Custom and automatic traces
   - **Network**: HTTP request performance (if configured)

## Best Practices

1. **Trace Naming**: Use descriptive, consistent names (e.g., `db_operation_name`, `screen_name_action`)
2. **Attributes**: Add context-specific attributes to help filter and analyze traces
3. **Metrics**: Track relevant metrics (counts, sizes, errors)
4. **Trace Duration**: Keep traces focused on specific operations
5. **Debug vs Release**: Performance monitoring is disabled in debug builds to avoid overhead
6. **Stop Traces**: Always stop traces in a `finally` block or use extension functions

## Adding New Traces

To add a new custom trace:

1. Inject `PerformanceManager` via Koin:
   ```kotlin
   val performanceManager: PerformanceManager by inject()
   ```

2. Start and stop the trace around your code:
   ```kotlin
   val trace = performanceManager.startTrace("my_operation")
   try {
       // Your code
       trace.putAttribute("context", "value")
       trace.putMetric("result_count", count)
   } finally {
       trace.stop()
   }
   ```

3. Or use extension functions for cleaner code:
   ```kotlin
   performanceManager.trace("my_operation") { trace ->
       // Your code
   }
   ```

## iOS Implementation

The current iOS implementation is a stub that logs to console. To enable full Firebase Performance on iOS:

1. Add Firebase Performance to your `Podfile`:
   ```ruby
   pod 'FirebasePerformance'
   ```

2. Initialize Firebase in `iOSApp.swift`

3. Replace the stub implementation in `PerformanceManager.ios.kt` with actual Firebase SDK calls

## Testing

Tests are located in `commonTest/kotlin/com/kobby/hymnal/core/performance/PerformanceManagerTest.kt`.

Run tests with:
```bash
./gradlew :composeApp:test
```

## Troubleshooting

### Traces Not Appearing in Firebase Console

1. Ensure you're running a **release build** (performance is disabled in debug)
2. Wait 24-48 hours for data to appear in the Firebase Console
3. Check that Firebase Performance is enabled in Firebase Console settings
4. Verify `google-services.json` is properly configured

### Performance Overhead

- Performance monitoring has minimal overhead in release builds
- It's completely disabled in debug builds
- Traces are sampled by Firebase to minimize impact

## Future Enhancements

Consider adding traces for:
- Heavy network operations
- Large image or asset loading
- Complex UI rendering operations
- Search and filtering operations
- Data synchronization
- Export/import operations

## References

- [Firebase Performance Documentation](https://firebase.google.com/docs/perf-mon)
- [Firebase Performance Android SDK](https://firebase.google.com/docs/perf-mon/get-started-android)
- [Firebase Performance iOS SDK](https://firebase.google.com/docs/perf-mon/get-started-ios)
