# Hymnal-CMP Project Analysis

**Generated:** October 28, 2025  
**Project Type:** Kotlin Multiplatform Mobile Application (Android & iOS)  
**Purpose:** Anglican Hymnal Digital Application

---

## Executive Summary

Hymnal-CMP is a production-ready Kotlin Multiplatform application that provides access to 841 Anglican liturgical texts across Android and iOS platforms. The application uses Compose Multiplatform for shared UI, SQLDelight for type-safe database access with full-text search, and follows modern architectural patterns with dependency injection via Koin.

---

## Technology Stack

### Core Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| **Kotlin** | 2.0.20 | Primary programming language |
| **Compose Multiplatform** | 1.7.3 | Cross-platform UI framework |
| **Android Gradle Plugin** | 8.13.0 | Build system |
| **Kotlin Multiplatform** | 2.0.20 | Code sharing across platforms |

### Key Dependencies

#### Navigation & Architecture
- **Voyager** 1.0.0 - Navigation and screen management
- **Koin** 3.5.6 - Dependency injection (BOM)
- **Kotlinx Coroutines** 1.10.2 - Asynchronous programming
- **Lifecycle** 2.8.4 - ViewModel and lifecycle management

#### Database & Storage
- **SQLDelight** 2.0.1 - Type-safe SQL database
- **Multiplatform Settings** 1.1.1 - Key-value storage

#### Analytics & Services
- **Firebase BOM** 33.13.0 - Firebase services platform
- **Firebase Analytics** - Usage tracking
- **Firebase App Distribution** 3.0.1 - Beta distribution

#### Build Tools
- **BuildKonfig** 0.17.1 - Build configuration generation
- **Foojay Toolchain Resolver** 0.9.0 - Java toolchain management

### Platform Targets

#### Android
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)
- **Compile SDK:** 35 (Android 15)

#### iOS
- **iosX64** - Intel-based Mac simulators
- **iosArm64** - Physical iOS devices
- **iosSimulatorArm64** - Apple Silicon Mac simulators

---

## Project Structure

```
Hymnal-CMP/
├── composeApp/                 # Main shared application module
│   ├── src/
│   │   ├── commonMain/         # Shared code (Android + iOS)
│   │   │   ├── kotlin/
│   │   │   │   └── com/kobby/hymnal/
│   │   │   │       ├── core/           # Core functionality
│   │   │   │       │   ├── database/   # Database layer (expect/actual)
│   │   │   │       │   ├── settings/   # User preferences
│   │   │   │       │   └── sharing/    # Share functionality
│   │   │   │       ├── data/           # Data layer & repository
│   │   │   │       ├── di/             # Dependency injection modules
│   │   │   │       ├── presentation/   # UI layer
│   │   │   │       │   ├── components/ # Reusable UI components
│   │   │   │       │   └── screens/    # Feature screens
│   │   │   │       │       ├── home/   # Home screen
│   │   │   │       │       ├── hymns/  # Hymn browsing & detail
│   │   │   │       │       ├── search/ # Global search
│   │   │   │       │       └── more/   # More menu (favorites, history, highlights)
│   │   │   │       ├── theme/          # App theming
│   │   │   │       ├── debug/          # Debug tools
│   │   │   │       ├── test/           # Test screens
│   │   │   │       └── App.kt          # App entry point
│   │   │   ├── composeResources/
│   │   │   │   └── files/
│   │   │   │       └── hymns.db        # Prepackaged SQLite database (1.8MB)
│   │   │   └── sqldelight/             # SQLDelight schema
│   │   ├── androidMain/        # Android-specific code
│   │   ├── iosMain/            # iOS-specific code
│   │   └── commonTest/         # Shared unit tests
│   └── build.gradle.kts        # Module build configuration
├── iosApp/                     # iOS application wrapper
│   ├── iosApp/
│   │   ├── iOSApp.swift        # iOS app entry point
│   │   ├── ContentView.swift   # iOS content view
│   │   └── GoogleService-Info.plist
│   └── iosApp.xcodeproj/       # Xcode project
├── scripts/
│   └── hymn_processor.py       # Python script for DB generation
├── gradle/
│   └── libs.versions.toml      # Version catalog
├── build.gradle.kts            # Root build configuration
├── settings.gradle.kts         # Project settings
└── release_notes               # Version history and release notes
```

---

## Architecture

### Overview
The application follows **Clean Architecture** principles with clear separation of concerns:

1. **Presentation Layer** - Compose Multiplatform UI
2. **Data Layer** - Repository pattern with SQLDelight
3. **Core Layer** - Cross-platform utilities (expect/actual pattern)

### Key Architectural Patterns

#### 1. Expect/Actual Pattern
Platform-specific implementations for:
- **DatabaseHelper** - Database file copying
- **DriverFactory** - SQLDelight driver creation
- **DatabaseInitializer** - Platform-specific initialization
- **ShareManager** - Native sharing functionality

#### 2. Repository Pattern
**HymnRepository** provides:
- Flow-based reactive data access
- Type-safe SQL queries via SQLDelight
- Full-text search (FTS4)
- Favorites, history, and highlights management

#### 3. Dependency Injection (Koin)
Modules:
- `DatabaseModule` - Database and repository instances
- `SettingsModule` - User preferences
- `AndroidModule` / `IosModule` - Platform-specific dependencies

#### 4. Navigation (Voyager)
- Screen-based navigation
- Slide transitions
- Type-safe screen parameters

---

## Database Architecture

### Strategy: Prepackaged Database

The application uses a **prepackaged database approach** for optimal performance and offline-first functionality:

1. **Offline Generation**: Python script (`hymn_processor.py`) processes hymn text files and generates `hymns.db`
2. **Resource Bundling**: Database file (1.8MB) bundled in `composeResources/files/`
3. **First Launch Copy**: On app initialization, database copied to platform-specific location
4. **SQLDelight Integration**: Type-safe, reactive database access

### Database Schema

#### Core Tables

**hymn**
- Primary content table (841 hymns)
- Fields: `id`, `number`, `title`, `category`, `content`, `created_at`
- Categories: `ancient_modern`, `supplementary`, `canticles`
- Indexed: `category`, `number`

**hymn_fts** (Virtual Table)
- FTS4 full-text search index
- Searches: `number`, `title`, `category`, `content`
- Automatically synced via triggers

**favorite**
- User-saved favorites
- Links to hymn via foreign key
- Unique constraint on `hymn_id`

**history**
- Access history tracking
- Timestamp-indexed for recent queries

**highlight**
- Text highlighting feature
- Stores: `start_index`, `end_index`, `color_index`
- Multiple highlights per hymn

### Query Categories

1. **Hymn Queries**: Browse by category, search, random hymn
2. **Favorite Queries**: Add, remove, list favorites
3. **History Queries**: Track access, get recent hymns
4. **Highlight Queries**: Save, retrieve, delete highlights
5. **Search Queries**: Full-text search with FTS4

---

## Feature Set

### Core Features

#### 1. Hymn Browsing
- **All Hymns List** - Browse complete collection (841 hymns)
- **Category Browsing**:
  - Ancient & Modern (779 hymns)
  - Supplementary (55 hymns)
  - Canticles (7 texts)
- **Hymn Detail View** - Full text display with formatting

#### 2. Search
- **Global Search** - FTS4-powered full-text search
- Search across: number, title, content
- Real-time search results

#### 3. Personalization
- **Favorites** - Save favorite hymns
- **History** - Recently viewed hymns
- **Highlights** - Text highlighting with color options
- **Font Settings** - Adjustable text size and style

#### 4. Additional Features
- **Share** - Native platform sharing
- **Random Hymn** - Discover hymns randomly
- **Onboarding** - First-time user experience
- **Debug Tools** - Database inspector (development)

### User Settings

Managed via **MultiplatformSettings**:
- Font size preferences
- Font family selection
- Onboarding completion status
- Other user preferences

---

## Build System

### Gradle Configuration

#### Version Catalog (`libs.versions.toml`)
- Centralized dependency management
- Type-safe accessors
- Version consistency across modules

#### Build Features

**Android:**
- Jetpack Compose
- ViewBinding (if needed)
- BuildConfig generation via BuildKonfig

**iOS:**
- Framework generation for Xcode
- Static framework (`isStatic = true`)
- Multiple architecture support

#### Custom Build Logic

**Version Management:**
```kotlin
readLatestAppVersion()       // Parses release_notes file
versionNameToCode()          // Converts semantic version to code
readLatestDevReleaseNotes()  // Extracts release notes for distribution
```

**Firebase App Distribution:**
- Debug builds auto-configured
- Release notes extracted from `release_notes` file
- Internal tester group distribution

---

## Development Workflow

### Data Management

#### Hymn Database Updates
```bash
cd scripts
python3 hymn_processor.py
# Processes: /Users/kobby/Desktop/Anglican hymn files
# Output: composeApp/src/commonMain/composeResources/files/hymns.db
```

### Building

#### Android
```bash
# Build debug APK
./gradlew :composeApp:assembleDebug

# Install on device
./gradlew installDebug

# Run tests
./gradlew :composeApp:testDebugUnitTest
```

#### iOS
```bash
# Build framework
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode

# Open in Xcode
open iosApp/iosApp.xcodeproj
```

### Testing

**Unit Tests:**
- `commonTest/` - Shared test code
- Repository tests with coroutines-test
- Database query validation

**Debug Tools:**
- Database Inspector Screen
- Test Hymn Database Screen

---

## Code Organization

### Package Structure

```
com.kobby.hymnal/
├── core/                   # Cross-platform utilities
│   ├── database/          # Database layer (expect/actual)
│   ├── settings/          # User preferences
│   └── sharing/           # Share functionality
├── data/                  # Data layer
│   └── ComposeRepository.kt
├── di/                    # Dependency injection
│   ├── DatabaseModule.kt
│   └── SettingsModule.kt
├── presentation/          # UI layer
│   ├── components/        # Reusable UI components
│   └── screens/           # Feature screens
├── theme/                 # Theming
│   ├── Colors.kt
│   ├── Typography.kt
│   ├── Shapes.kt
│   └── Dimens.kt
├── debug/                 # Debug utilities
├── test/                  # Test screens
└── App.kt                 # Application entry point
```

### Naming Conventions

- **Screens**: `*Screen.kt` (e.g., `HomeScreen`, `HymnDetailScreen`)
- **Components**: Descriptive names (e.g., `ListItem`, `SearchTextField`)
- **ViewModels**: `*ViewModel.kt` (if using ViewModels)
- **Repositories**: `*Repository.kt`
- **Modules**: `*Module.kt` (Koin DI)

---

## Configuration Files

### Key Files

**Build Configuration:**
- `build.gradle.kts` - Root build file
- `settings.gradle.kts` - Project settings
- `gradle.properties` - Gradle properties
- `libs.versions.toml` - Version catalog

**Platform-Specific:**
- `google-services.json` - Firebase config (Android)
- `GoogleService-Info.plist` - Firebase config (iOS)
- `AndroidManifest.xml` - Android manifest
- `Info.plist` - iOS app info

**Development:**
- `CLAUDE.md` - Claude AI context
- `GEMINI.md` - Gemini AI context
- `release_notes` - Version history
- `local.properties` - Local SDK paths

---

## Data Content

### Hymn Collection

**Total:** 841 liturgical texts

**Breakdown:**
- **Ancient & Modern:** 779 hymns (numbered 1-779)
- **Supplementary:** 55 hymns (numbered 801-855)
- **Canticles:** 7 texts (Te Deum, Benedictus, Magnificat, Nunc Dimittis, etc.)

**Data Processing:**
- Source: Text files on developer's desktop
- Processor: `scripts/hymn_processor.py`
- Database: SQLite with FTS4 indexing
- Size: ~1.8MB compressed

---

## Firebase Integration

### Services Used

**Firebase Analytics:**
- User engagement tracking
- Screen view tracking
- Custom events

**Firebase App Distribution:**
- Internal testing distribution
- Debug build distribution
- Automated release notes

### Configuration

**Android:** `composeApp/google-services.json`  
**iOS:** `iosApp/iosApp/GoogleService-Info.plist`

Both platforms initialized in respective entry points:
- Android: `MainActivity.onCreate()`
- iOS: `iOSApp.init()`

---

## Platform-Specific Implementations

### Android (`androidMain`)

**Components:**
- `MainActivity.kt` - Main activity with Compose integration
- `AndroidModule.kt` - Android-specific DI
- `DatabaseHelper.android.kt` - Database copying to internal storage
- `DriverFactory.android.kt` - AndroidSqliteDriver creation
- `ShareManager.android.kt` - Android share sheet integration
- Compose previews for development

**Resources:**
- `AndroidManifest.xml` - App manifest
- `res/` - Android resources
- `google-services.json` - Firebase configuration

### iOS (`iosMain`)

**Components:**
- `MainViewController.kt` - UIViewController wrapper
- `IosModule.kt` - iOS-specific DI
- `DatabaseHelper.ios.kt` - Database copying to documents
- `DriverFactory.ios.kt` - NativeSqliteDriver creation
- `ShareManager.ios.kt` - UIActivityViewController integration

**Swift Components:**
- `iOSApp.swift` - SwiftUI app entry point
- `ContentView.swift` - Compose integration
- `GoogleService-Info.plist` - Firebase configuration

---

## Testing Strategy

### Unit Tests (`commonTest`)

**Coverage:**
- Repository layer tests
- Database query validation
- Coroutines-based async testing

**Test Dependencies:**
- `kotlin-test`
- `kotlinx-coroutines-test` 1.8.0

### Manual Testing

**Debug Screens:**
- Database Inspector - SQL query execution
- Test Hymn Database - Data validation

---

## Performance Considerations

### Database Optimization

1. **Prepackaged Database** - No network latency
2. **Indexed Queries** - Fast category and number lookups
3. **FTS4 Search** - Optimized full-text search
4. **Flow-based Queries** - Reactive, memory-efficient

### UI Performance

1. **LazyColumn** - Efficient list rendering
2. **Coil/Kamel** - Image loading (if applicable)
3. **Coroutine Dispatchers** - Proper threading

### App Size

- **APK Size:** ~15-20MB (estimated)
- **Database:** 1.8MB
- **Framework:** Compact Compose runtime

---

## Future Considerations

### Potential Enhancements

1. **Offline-First Sync** - Cloud backup for favorites/highlights
2. **Audio Integration** - Hymn tune playback
3. **Scripture References** - Linked biblical texts
4. **Themes** - Dark mode, custom color schemes
5. **Accessibility** - Screen reader optimization, high contrast
6. **Localization** - Multi-language support
7. **Social Features** - Share favorites with friends
8. **Analytics Enhancement** - Detailed usage insights

### Technical Debt

1. **Remove deprecated DatabaseManager** - Replaced by Koin DI
2. **Migrate to Compose Navigation** - Consider replacing Voyager
3. **Add integration tests** - E2E testing
4. **CI/CD Pipeline** - Automated builds and distribution
5. **Documentation** - API documentation (KDoc)

---

## Development Guidelines

### Code Style

- Follow Kotlin coding conventions
- Use meaningful variable names
- Document complex logic with comments
- Keep functions small and focused

### Commit Messages

Follow Chris Beams' seven rules:
1. Separate subject from body with blank line
2. Limit subject line to 50 characters
3. Capitalize subject line
4. No period at end of subject
5. Use imperative mood ("Add feature" not "Added feature")
6. Wrap body at 72 characters
7. Explain what and why, not how

### Testing Requirements

- Write unit tests for repositories
- Test database queries
- Validate platform-specific implementations
- Manual testing on both platforms

---

## Dependencies Graph

```
HymnalApp
├── Compose Multiplatform (UI)
├── Voyager (Navigation)
├── Koin (DI)
│   ├── HymnRepository
│   │   └── SQLDelight Database
│   ├── FontSettingsManager
│   │   └── Multiplatform Settings
│   └── Platform-specific modules
├── Firebase Analytics
└── Platform Entry Points
    ├── Android (MainActivity)
    └── iOS (iOSApp.swift → ContentView)
```

---

## Resources

### Documentation
- [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Voyager](https://voyager.adriel.cafe/)
- [Koin](https://insert-koin.io/)

### Project-Specific Docs
- `CLAUDE.md` - Claude AI guidance
- `GEMINI.md` - Gemini AI guidance
- `README.md` - Basic project info
- `release_notes` - Version history

---

## Conclusion

Hymnal-CMP is a well-architected Kotlin Multiplatform application demonstrating best practices in cross-platform mobile development. The use of modern libraries (Compose Multiplatform, SQLDelight, Koin, Voyager) combined with a clean architectural approach makes it maintainable and scalable. The prepackaged database strategy ensures excellent performance and offline capability, while the expect/actual pattern enables proper platform-specific implementations where needed.

The project serves as an excellent reference for building production-ready KMP applications with shared UI and business logic across Android and iOS.

