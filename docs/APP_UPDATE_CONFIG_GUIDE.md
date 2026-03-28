# App Update Configuration Guide

This guide explains how to manage app update prompts for the Anglican Hymnal app using Firebase Remote Config.

## Overview

The app uses a shared `UpdateManager` to check for new versions on startup. It fetches configuration from Firebase Remote Config to determine if an update is available and whether it is optional or mandatory.

## Remote Config Keys

You must configure the following two parameters in the Firebase Console under **Remote Config**:

| Parameter Key | Type | Default Value | Description |
| :--- | :--- | :--- | :--- |
| `latest_version` | String | `0.9.11` | The newest version currently available in the stores (e.g., "1.0.5"). |
| `min_required_version` | String | `0.9.0` | The minimum version required to use the app. Any version older than this will be forced to update. |

## Update Types

### 1. Optional Update
**Condition**: `current_version` < `latest_version` AND `current_version` >= `min_required_version`
- **UI Behavior**: Shows a dialog with "Update Now" and "Later" buttons.
- **Messaging**: "A new version (vX.Y.Z) of the Anglican Hymnal is available. Update now to get the latest features and bug fixes!"
- **Dismissible**: Yes, the user can tap "Later" to continue using the app.

### 2. Mandatory Update
**Condition**: `current_version` < `min_required_version`
- **UI Behavior**: Shows a dialog with only the "Update Now" button.
- **Messaging**: "A critical update (vX.Y.Z) is available. You must update the app to continue using it."
- **Dismissible**: No, the "Later" button is hidden, and tapping outside the dialog will not dismiss it.

## How to Release a New Update

When you publish a new version to the Google Play Store or Apple App Store:

1. **Wait** for the update to be approved and visible in the store.
2. **Update `latest_version`**: Set this to the new version string (e.g., `1.1.0`) in the Firebase Console and publish the changes.
3. **Optional: Update `min_required_version`**: If the new release fixes a critical security flaw or a breaking bug, set this to the new version string to force all users to update.

## Versioning Format

The app uses **Semantic Versioning** (Major.Minor.Patch). Ensure the strings in Remote Config follow this format:
- ✅ `1.0.0`
- ✅ `0.9.11`
- ❌ `1.0` (Should be `1.0.0`)
- ❌ `v1.0.0` (Do not include the 'v' prefix)

## Testing the Prompts

To test the update prompt locally:
1. Ensure your local `BuildKonfig.VERSION_NAME` (found in `release_notes` or `composeApp/build.gradle.kts`) is lower than what you set in Remote Config.
2. Launch the app.
3. Check the logs for `UpdateManager` tags to see the values being fetched.

> **Note**: Remote Config has a default fetch interval (typically 12 hours, but set to 1 hour in this app). For immediate testing, you may need to clear app data or use a Firebase debug build.
