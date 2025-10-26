# Gemini Project Context: Hymnal-CMP

This document provides context for the Hymnal-CMP project, a Kotlin Multiplatform application for Android and iOS.

## Project Overview

Hymnal-CMP is a mobile hymnal application built with Kotlin Multiplatform, allowing code sharing between Android and iOS. The user interface is built with Jetpack Compose and is shared across both platforms. The app displays hymns from a local SQLite database.

### Core Technologies

*   **Kotlin Multiplatform:** For sharing code between Android and iOS.
*   **Jetpack Compose:** For building the user interface for both Android and iOS.
*   **SQLDelight:** For the local SQLite database to store hymns. The database is pre-populated by a Python script.
*   **Voyager:** For navigation within the Compose application.
*   **Firebase Analytics:** For usage analytics.
*   **Multiplatform Settings:** For simple key-value data storage.

### Project Structure

*   `composeApp`: The main shared module containing the common code, including UI, business logic, and database access.
*   `iosApp`: The Xcode project for the iOS application.
*   `scripts/hymn_processor.py`: A Python script that processes text files containing hymns, and generates the `hymns.db` SQLite database file. This database is then included in the `composeApp/src/commonMain/composeResources/files` directory.

## Building and Running

The project is built using Gradle.

### Android

To build and run the application on an Android device or emulator:

1.  Build the project:
    ```bash
    ./gradlew build
    ```
2.  Install the debug APK:
    ```bash
    ./gradlew :composeApp:installDebug
    ```
3.  Run the app from the device's app drawer.

### iOS

To run the application on an iOS simulator or device:

1.  Open the Xcode project:
    ```bash
    open iosApp/iosApp.xcodeproj
    ```
2.  Select a simulator or a connected device.
3.  Click the "Run" button in Xcode.

### Running Tests

To run the unit tests:

```bash
./gradlew test
```

## Development Conventions

### Commit Messages

This project follows the seven rules for great commit messages as described by Chris Beams. A summary is available in `.claude/commands/git-commit.md`.

1.  Separate subject from body with a blank line.
2.  Limit the subject line to 50 characters.
3.  Capitalize the subject line.
4.  Do not end the subject line with a period.
5.  Use the imperative mood in the subject line.
6.  Wrap the body at 72 characters.
7.  Use the body to explain what and why vs. how.
