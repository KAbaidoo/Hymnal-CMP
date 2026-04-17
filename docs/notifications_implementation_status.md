# Notifications Implementation Status

## Product Decisions (Confirmed)

1. Campaign notifications are externally controlled and should still be delivered once OS-level permission is granted, regardless of in-app notification toggle state.
2. A single global notification toggle controls all internal notification scheduling and preferences (no per-type in-app toggles).
3. Android weekly reminders intentionally use WorkManager for best-effort Sunday-morning delivery (not exact alarm semantics).

## Findings (Ordered by Severity)

1. Medium: Notification tests now cover shared seasonal engine, preferences, and scheduler orchestration; platform campaign delivery hooks and end-to-end analytics delivery still rely on integration/device validation.

## Task Checklist

- [x] Task 1: Define notification contract (types, IDs, channels, defaults)
- [x] Task 2: Expand preferences model (global toggle + last-active)
- [x] Task 3: Implement seasonal date engine (Christmas + Easter-derived events) with tests
- [x] Task 4: Implement Sunday morning scheduling on Android (WorkManager-based)
- [x] Task 5: Implement inactivity nudge based on last-active timestamp on Android
- [x] Task 6: Implement seasonal scheduling on Android from shared seasonal engine
- [x] Task 7: Complete Android notification runtime permission UX path
- [x] Task 8: Scope Android cancellation to notification work only
- [x] Task 9: Wire iOS local notifications end-to-end (provider init + scheduling/cancel)
- [x] Task 10: Implement Android FCM campaigns (token, receive, display; external-permission-controlled campaign delivery)
- [x] Task 11: Implement iOS FCM/APNs pipeline (token, receive hooks; external-permission-controlled campaign delivery)
- [x] Task 12: Add analytics events for schedule/receive/open/opt changes with Android+iOS parity
- [x] Task 13: Add tests for shared notification logic and preference behavior

## Verification Log

- ✅ `./gradlew :composeApp:compileDebugKotlinAndroid`
- ✅ `./gradlew :composeApp:compileKotlinIosSimulatorArm64`
- ✅ `xcodebuild -project iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' build CODE_SIGNING_ALLOWED=NO`
- ⚠️ `./gradlew :composeApp:testDebugUnitTest` fails due pre-existing unrelated test compilation issues in database tests (`HymnRepositoryTest`/`SafeHymnRepositoryTest`).
