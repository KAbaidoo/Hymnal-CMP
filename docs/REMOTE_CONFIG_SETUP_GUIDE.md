# Firebase Remote Config Setup Guide

This guide provides the necessary information to configure the Firebase Remote Config parameters for the Hymnal-CMP application. These keys allow you to dynamically control notification behavior, messaging, and app update prompts without releasing a new version of the app.

---

## 1. Notification Parameters

These parameters control the local notification reminders (Weekly, Inactivity, and Seasonal).

| Parameter Key | Type | Default Value | Description |
| :--- | :--- | :--- | :--- |
| `notifications_weekly_enabled` | Boolean | `true` | Globally enable or disable Sunday morning reminders. |
| `notifications_weekly_title` | String | "Your hymns are right here in your pocket" | The title displayed in the weekly Sunday notification. |
| `notifications_weekly_body` | String | "Open Hymnal and prepare for Sunday worship." | The message body for the weekly Sunday notification. |
| `notifications_inactivity_enabled` | Boolean | `true` | Globally enable or disable inactivity "nudges". |
| `notifications_inactivity_title` | String | "Start your morning with a hymn" | The title for the inactivity nudge notification. |
| `notifications_inactivity_body` | String | "Discover a hymn for today." | The message body for the inactivity nudge notification. |
| `notifications_inactivity_days` | Number (Long) | `3` | Number of days of inactivity before a nudge is triggered. |
| `notifications_seasonal_enabled` | Boolean | `true` | Globally enable or disable seasonal feast reminders (Christmas, Easter, etc.). |

---

## 2. App Update Parameters

These parameters control the version gating and update prompts.

| Parameter Key | Type | Default Value | Description |
| :--- | :--- | :--- | :--- |
| `min_required_version` | String | Current App Version | The minimum version required to use the app. Users on versions lower than this will see a **mandatory** update dialog. |
| `latest_version` | String | Current App Version | The latest available version. Users on versions lower than this (but higher than `min_required_version`) will see a **dismissible** update prompt. |

---

## 3. How to Set Up in Firebase Console

1.  **Open Firebase Console:** Go to your project in the [Firebase Console](https://console.firebase.google.com/).
2.  **Navigate to Remote Config:** In the left-hand sidebar, go to **Release & Monitor** > **Remote Config**.
3.  **Add Parameters:**
    *   Click **Add parameter**.
    *   Enter the **Parameter key** (e.g., `notifications_inactivity_days`).
    *   Select the **Data type** (Boolean, String, or Number).
    *   Enter the **Default value**.
    *   Click **Save**.
4.  **Publish Changes:**
    *   After adding or modifying parameters, click the **Publish changes** button in the top right to make them live for your users.

---

## 4. Technical Details (Fetch Policy)

*   **Caching:** The app fetches Remote Config values once per session.
*   **Android:** Values are cached for **12 hours** in production and **0 seconds** in debug builds.
*   **iOS:** Values are cached for **12 hours** in production and **0 seconds** in debug builds.
*   **Background Workers:** On Android, background workers (WorkManager) read from the **last successfully fetched values** stored in local preferences. This ensures notifications always have a message to display even if the device is offline when the worker triggers.

---

## 5. Best Practices

*   **Seasonal Content:** You can update `notifications_weekly_body` to mention specific seasons (e.g., "Open Hymnal for your Lenten devotions") to keep the app feeling fresh.
*   **Testing:** Use the **Debug** build of the app to see Remote Config changes immediately without waiting for the 12-hour cache to expire.
*   **Rollback:** If you accidentally disable a feature or set a wrong message, simply revert the value in the Firebase Console and **Publish changes**.
