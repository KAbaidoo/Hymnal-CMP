This is a Kotlin Multiplatform project targeting Android, iOS.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.


Monetization / Donation model

- All features in the app are free for everyone — no feature gates or locks.
- Users will see gentle, dismissible donation prompts over time using exponential backoff (10, 25, 50, 100, 200, 400 hymns).
- Once a user donates (or restores a prior donation), they will not be shown donation prompts again.
- Pricing (Ghana market): GH₵ 10 (basic), GH₵ 20 (generous). Payment methods include MTN MoMo and Telecel Cash.

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…