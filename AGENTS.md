# Repository Guidelines

## Project Structure & Module Organization
- `composeApp/`: Shared Kotlin Multiplatform code (Compose UI, repositories, SQLDelight, resources).
- `composeApp/src/commonMain/`: Shared Kotlin sources and `composeResources/files/hymns.db`.
- `composeApp/src/commonTest/`: Shared unit tests.
- `composeApp/src/androidMain/`, `composeApp/src/iosMain/`: Platform-specific implementations.
- `iosApp/`: iOS wrapper and SwiftUI integration.
- `hymnal_data/`: Source hymn texts used to generate the database.
- `scripts/`: Data processing utilities (e.g., hymn DB generation).
- `docs/`, `release_notes/`, `build/`: Documentation, release history, build outputs.

## Build, Test, and Development Commands
```bash
# Build all targets
./gradlew build

# Build Android debug APK
./gradlew :composeApp:assembleDebug

# Run shared unit tests
./gradlew :composeApp:testDebugUnitTest

# Build iOS framework for Xcode
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
```
Use Xcode to run the iOS app from `iosApp/`.

## Coding Style & Naming Conventions
- Kotlin: 4-space indentation, standard Kotlin/Compose formatting (use IDE formatter).
- SwiftUI: follow Swift naming conventions and file-level formatting.
- Naming patterns:
  - Kotlin files/types: `PascalCase` (e.g., `HymnDetailScreen.kt`).
  - Functions/vars: `camelCase`.
  - Packages: lowercase with dots (`com.kobby.hymnal`).
  - Compose `@Composable` functions: `PascalCase`.

## Testing Guidelines
- Framework: Gradle/Kotlin test tasks under `composeApp/src/commonTest/`.
- Naming: mirror source names when possible (e.g., `HymnRepositoryTest.kt`).
- Run tests with `./gradlew :composeApp:testDebugUnitTest`.

## Commit & Pull Request Guidelines
- Commit style: Conventional Commits with scope, e.g. `feat(paywall): update spacing`, `fix(purchase): restore purchases`.
- Pull requests should include:
  - Clear description of changes and rationale.
  - Linked issue (if applicable).
  - Screenshots or short screen recordings for UI changes (Android and/or iOS).
  - Notes on database/schema changes and any migration implications.

## Data & Database Workflow
- Hymn data is generated from `hymnal_data/` using `scripts/hymn_processor.py`.
- Output database lives at `composeApp/src/commonMain/composeResources/files/hymns.db`.
```
cd scripts
python3 hymn_processor.py
```
