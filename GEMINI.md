# GEMINI.md - Hymnal-CMP Project Context

This file provides essential context and instructions for Gemini CLI when working on the Hymnal-CMP project.

## Project Overview
**Hymnal-CMP** is a Kotlin Multiplatform (KMP) application providing a digital Anglican hymnal for Android and iOS. It features 991 liturgical texts (Ancient & Modern hymns, Supplementary hymns, Canticles, and Psalms) with offline search capabilities and a donation-based monetization model focused on the Ghanaian market.

### Core Technologies
- **UI Framework:** Compose Multiplatform (shared UI for Android and iOS).
- **Navigation:** Voyager (ID-based screen navigation).
- **Dependency Injection:** Koin.
- **Local Database:** SQLDelight with a prepackaged SQLite database (`hymns.db`) and FTS4 search.
- **Settings:** Multiplatform Settings for user preferences.
- **Analytics/Crash Reporting:** Firebase (Analytics, Crashlytics).
- **Configuration:** BuildKonfig for compile-time constants.

### Architecture & Patterns
- **Repository-First Screen Pattern:** Screens accept primitive IDs (for Voyager serialization) and load data internally via repositories.
- **Prepackaged Database Strategy:** Hymn data is processed offline via Python scripts and bundled in `composeResources/files/hymns.db`. On first launch, it is copied to the platform-specific data directory.
- **Reactive Data:** Repositories provide `Flow`-based streams of data from the database.

## Directory Structure
- `composeApp/`: Shared KMP module.
    - `src/commonMain/`: Shared logic, UI, and `composeResources/files/hymns.db`.
    - `src/commonTest/`: Shared unit tests.
    - `src/androidMain/` & `src/iosMain/`: Platform-specific implementations (e.g., database drivers, helpers).
    - `src/sqldelight/`: Database schema and SQL queries.
- `iosApp/`: iOS-specific wrapper and SwiftUI entry point.
- `hymnal_data/`: Source text files for hymns, canticles, and psalms.
- `scripts/`: Python utilities for processing source texts into the SQLite database.
- `docs/`: Detailed implementation plans, guides, and architectural decisions.

## Building and Running
### Build Commands
- **Full Build:** `./gradlew build`
- **Android Debug APK:** `./gradlew :composeApp:assembleDebug`
- **Android Install:** `./gradlew installDebug`
- **iOS Framework (for Xcode):** `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode`

### Testing
- **Shared Unit Tests:** `./gradlew :composeApp:testDebugUnitTest`

### Database Management
- **Regenerate Database:**
  ```bash
  cd scripts
  python3 hymn_processor.py
  ```
  This processes source files in `hymnal_data/` and updates `composeApp/src/commonMain/composeResources/files/hymns.db`.

## Development Conventions
- **Coding Style:** Standard Kotlin/Compose conventions. 4-space indentation.
- **Naming:**
    - `@Composable` functions & Screens: `PascalCase`.
    - Regular functions/variables: `camelCase`.
    - File names: `PascalCase`.
- **Navigation:** Always use primitive types (e.g., `Long` IDs) as Voyager screen parameters to ensure serialization safety.
- **Commits:** Use Conventional Commits (e.g., `feat(ui): add search filter`, `fix(db): correct hymn 402 text`).
- **Data Integrity:** Any changes to hymn content should be made in `hymnal_data/` and the database regenerated using the Python script.

## Key Implementation Details
- **Canticles Numbering:** 1001-1007 (liturgical order).
- **Donation Model:** Gentle, dismissible prompts at specific milestones (10, 30, 60, 100, 150 hymns read). Prompts are capped at 150 hymns per year for non-supporters. Counters reset annually for non-supporters. Pricing targets the Ghanaian market (GH₵ 10/20).
- **FTS Search:** Full-text search is implemented via a virtual table `hymn_fts` with SQLite triggers.
