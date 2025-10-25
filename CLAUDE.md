# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Kotlin Multiplatform Compose application called "Hymnal-CMP" - an Anglican hymnal app targeting Android and iOS platforms. The app contains 841 Anglican liturgical texts (779 Ancient & Modern hymns + 55 Supplementary hymns + 7 Canticles) stored in a prepackaged SQLite database with full-text search capabilities.

## Architecture

- **Kotlin Multiplatform**: Shared business logic and UI across Android and iOS
- **Compose Multiplatform**: Declarative UI framework for cross-platform development
- **Voyager**: Navigation library for screen management and transitions
- **SQLDelight**: Type-safe database access with prepackaged hymn data
- **Firebase**: Analytics integration
- **Settings**: Multiplatform settings storage for user preferences

### Database Architecture (Key Implementation Detail)

The app uses a **prepackaged database strategy**:
- Hymn data is processed offline using `scripts/hymn_processor.py`
- Database file (`1.8MB`) is bundled in `composeResources/files/hymns.db`
- On first launch, database is copied to app's data directory
- SQLDelight provides type-safe access with FTS4 full-text search

**Database Layer Structure:**
- `DatabaseManager`: Singleton for database/repository access
- `DatabaseHelper`: Platform-specific database copying (expect/actual)
- `DriverFactory`: Platform-specific SQLDelight driver creation
- `HymnRepository`: Repository pattern with Flow-based reactive queries
- `DatabaseInitializer`: Coordinates initialization across platforms

### Project Structure

- `composeApp/`: Main shared module containing the app logic
  - `src/commonMain/`: Shared code for all platforms
    - `kotlin/com/kobby/hymnal/`: Main application package
      - `core/database/`: Database layer with expect/actual implementations
      - `debug/`: Database inspector and testing tools
      - `test/`: Testing screens and utilities
      - `theme/`: App theming (Colors, Typography, Shapes)
    - `composeResources/files/`: Contains prepackaged `hymns.db` (1.7MB)
    - `sqldelight/`: Database schema with FTS4 search tables
  - `src/androidMain/`: Android-specific database helpers and UI
  - `src/iosMain/`: iOS-specific database helpers
  - `src/commonTest/`: Unit tests for repository and database
- `iosApp/`: iOS application wrapper and SwiftUI integration
- `scripts/`: Data processing scripts for generating hymn database

## Key Dependencies

- Compose Multiplatform 1.7.3
- Kotlin 2.0.20
- Voyager 1.0.0 (navigation)
- SQLDelight 2.0.1 (database)
- Firebase BOM 33.13.0
- Multiplatform Settings 1.1.1

## Common Development Commands

### Build Commands
```bash
# Build the project
./gradlew build

# Build Android APK for testing
./gradlew :composeApp:assembleDebug

# Install on connected Android device
./gradlew installDebug

# Build for iOS (requires Xcode)
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
```

### Testing Commands
```bash
# Run unit tests
./gradlew :composeApp:testDebugUnitTest

# Build test APK and inspect database
./gradlew :composeApp:assembleDebug
# Then launch app → "Test Hymn Database" → "Database Inspector"
```

### Database Development

**Regenerating Hymn Database:**
```bash
cd scripts
python3 hymn_processor.py
# Processes /Users/kobby/Desktop/Anglican hymn files and canticles
# Outputs to composeApp/src/commonMain/composeResources/files/hymns.db
```

**Database Inspection:**
```bash
sqlite3 composeApp/src/commonMain/composeResources/files/hymns.db
# Check total count: SELECT COUNT(*) FROM hymn;
# Category breakdown: SELECT category, COUNT(*) FROM hymn GROUP BY category;
# View canticles: SELECT number, title FROM hymn WHERE category = 'canticles' ORDER BY number;
```

### Database Schema Key Points
- `hymn` table: Core hymn data with FTS4 search support
- `favorite`, `history`, `highlight` tables: User interaction data
- Indexed on category and number for performance
- Full-text search via `hymn_fts` virtual table with triggers

### Platform-Specific Database Behavior
- **Android**: Database copied from assets via `DatabaseHelper.android.kt`
- **iOS**: Database copied from bundle via `DatabaseHelper.ios.kt` 
- **Initialization**: Handled by `DatabaseInitializer` with platform-specific contexts
- **Error Handling**: Loading states and error recovery in `App.kt`

### Navigation & Testing
- Uses Voyager for navigation between screens
- Screen implementations extend `cafe.adriel.voyager.core.screen.Screen`
- **Testing Infrastructure**: 
  - `TestHymnScreen`: Basic database functionality testing
  - `DatabaseInspectorScreen`: Advanced database analysis and search performance
  - Navigation: Main → Test → Inspector

### Settings & Configuration
- Multiplatform Settings library for persistent storage
- Firebase configuration files present for both platforms
- Android: `google-services.json`
- iOS: `GoogleService-Info.plist`

## App Features
- Prepackaged database with 841 Anglican liturgical texts
- Full-text search with performance monitoring  
- Category filtering (Ancient & Modern, Supplementary, Canticles)
- Favorites and history tracking with SQLDelight queries
- Text highlighting capabilities
- Database inspector for debugging and performance analysis
- Cross-platform database initialization and error handling

## Canticles Numbering System
- Canticles are assigned numbers 1001-1007 in liturgical order:
  - 1001: Venite (call to worship)
  - 1002: Te Deum Laudamus (great hymn of praise)
  - 1003: Jubilate Deo (alternative to Te Deum)  
  - 1004: Benedictus (Zechariah's song)
  - 1005: Magnificat (Mary's song)
  - 1006: Nunc dimittis (Simeon's song)
  - 1007: The Creed (statement of faith)