Store Setup Guide — App Store Connect & Google Play Console

Purpose

This guide documents how to create and verify the in‑app products used by the Hymnal-CMP app in both Apple App Store Connect and Google Play Console. It uses the app's canonical product IDs and explains platform differences, test flows (purchase/restore), and how to verify entitlements in the app.

Canonical values (from the code)

- Product IDs (canonical):
  - support_basic
  - support_generous

- Product types (as implemented in the repo):
  - iOS: Non‑consumable (NonConsumable) — see `iosApp/iosApp/Core/iap/MyProducts.storekit` (type: "NonConsumable").
  - Android: One‑time in‑app purchases (BillingClient.ProductType.INAPP) — see `composeApp/src/androidMain/kotlin/com/kobby/hymnal/core/iap/BillingHelper.kt` and `PurchaseManager.android.kt`.

- Code references (authoritative):
  - Android product constants and purchase flow: `composeApp/src/androidMain/kotlin/com/kobby/hymnal/core/iap/BillingHelper.kt` and `PurchaseManager.android.kt`.
  - iOS product constants and restore/purchase flow: `composeApp/src/iosMain/kotlin/com/kobby/hymnal/core/iap/PurchaseManager.ios.kt` and `iosApp/iosApp/Core/iap/IosPurchaseProvider.swift`.
  - Local test StoreKit config: `iosApp/iosApp/Core/iap/MyProducts.storekit`.
  - Persistence & entitlement keys: `composeApp/src/commonMain/kotlin/com/kobby/hymnal/core/iap/PurchaseStorage.kt`.

High‑level decisions (already implemented)

- Both support tiers are implemented as one‑time purchases (not recurring subscriptions).
- The app treats a recorded purchase as `EntitlementState.SUPPORTED` and all app features remain accessible regardless of support status (supporter status only affects donation prompts). See `EntitlementState.kt`.

Part A — App Store Connect (iOS)

Goal: Create two non‑consumable in‑app purchases that match the app's product IDs and test them via local StoreKit configuration and TestFlight/sandbox testing.

Steps (App Store Connect)
1. Sign in to App Store Connect and open your App record.
2. Go to "Features" → "In‑App Purchases".
3. Click the "+" button and choose "Non‑Consumable".
   - Product ID: support_basic
   - Reference Name: Basic Support (or "Basic Support (GH₵ 10)")
   - Display Price: set to appropriate price tier (GH₵ 10 / $0.99 equivalent). Configure country prices if needed.
   - Localizations: add English (en_US) description/title matching the app strings.
   - Upload a screenshot if App Store Connect requires it for review (non‑consumable typically doesn't need one for sandbox testing but helpful for review).
4. Save and repeat for support_generous (product ID: support_generous). Display price should be GH₵ 20 / $1.99 equivalent.

### Product metadata — suggested titles and descriptions

Use the copy below when creating the product entries in App Store Connect and Google Play Console. These are suggested, store‑friendly strings aligned with the app's in‑app messaging. Edit any localized wording as needed.

A. App Store Connect (suggested fields)

- Basic Support (product ID: `support_basic`) — Recommended metadata:
  - Reference Name: Basic Support (GH₵ 10)
  - Product ID: support_basic
  - Purchase option ID *: support_basic
  - Localized Title (en_US): Basic Support
  - Subtitle / Short Summary: GH₵ 10 — One‑time
  - Description (long): "Support the Anglican Hymnal project with a small one‑time contribution. This purchase helps keep hymns freely available and supports ongoing maintenance and improvements. Thank you for supporting this ministry."
  - Review Notes (to App Review): "Non‑consumable one‑time contribution that marks the user as a supporter in‑app. Does not unlock copyright‑restricted content — it only removes donation prompts and supports development."
  - Pricing: Set to GH₵ 10 (or equivalent price tier). Ensure country currencies are correct.
  - Screenshot: Optional — a small, clear UI screenshot of the Support screen or PayWall header helps reviewers.

- Generous Support (product ID: `support_generous`) — Recommended metadata:
  - Reference Name: Generous Support (GH₵ 20)
  - Product ID: support_generous
  - Purchase option ID *: support_generous
  - Localized Title (en_US): Generous Support
  - Subtitle / Short Summary: GH₵ 20 — One‑time
  - Description (long): "Make a generous one‑time contribution to support the Anglican Hymnal. Your support helps add more hymns, maintain the app, and keep worship resources free for everyone. Thank you for your generosity."
  - Review Notes: "Non‑consumable one‑time contribution. Same in‑app effect as Basic Support (supporter status), only different contribution level."
  - Pricing: GH₵ 20 (or equivalent USD tier).
  - Screenshot: Optional — a PayWall screenshot showing both tiers.

Notes for App Store Connect copy:
- Keep the title and reference concise; use the longer description to explain what the contribution supports.
- Use the Review Notes field to explain the intent (donation/supporter flag) so App Review understands this is not a content unlock requiring additional metadata.

B. Google Play Console (suggested fields)

- Basic Support (product ID: `support_basic`) — Suggested Play metadata:
  - Title: Basic Support — GH₵ 10
  - Purchase option ID *: support_basic
  - Short Description: "One‑time support to keep the hymnal free"
  - Full Description: "Support the Anglican Hymnal with a one‑time contribution of GH₵ 10. This helps us maintain the app, add hymns, and keep the core experience free for everyone. Thank you for supporting this ministry."
  - Product ID: support_basic
  - Graphics: Provide a small feature graphic and an optional screenshot of the PayWall or Support screen.
  - Pricing: GH₵ 10 (or equivalent)

- Generous Support (product ID: `support_generous`) — Suggested Play metadata:
  - Title: Generous Support — GH₵ 20
  - Purchase option ID *: support_generous
  - Short Description: "Make a generous one‑time contribution"
  - Full Description: "Make a generous one‑time contribution to the Anglican Hymnal. Your support helps fund hymnal additions, feature work, and maintenance so we can keep worship resources freely available. Thank you."
  - Product ID: support_generous
  - Graphics: Same as above; consider highlighting "Generous" tier in the PayWall screenshot.

Notes for Google Play copy:
- Use the short description for quick listing display; the full description can include a short bullet list of benefits (e.g., "Keeps app free; Adds hymns; Maintains the app").
- Ensure the product is activated in Play Console and attached to the uploaded AAB test release.

Common guidance for both stores

- Keep messaging consistent with in‑app copy ("Support the Anglican Hymnal", "Keep this app free for everyone").
- Make clear that these are one‑time, non‑consumable contributions that set the user as a supporter in the app (no recurring charges).
- For Review Notes to App Store Connect and Play Console support, explain that the purchase does not gate essential content — it records supporter status which affects donation prompts and usage analytics only.
- Provide a PayWall screenshot showing both tiers and the CTA; this helps reviewers and testers find the right UI during review.

Testing methods (iOS)

A. Local StoreKit testing (fast, no App Upload required)
- Open the project in Xcode and set the Scheme's StoreKit Configuration to the file at `iosApp/iosApp/Core/iap/MyProducts.storekit`.
- Run the app on a device or simulator using that scheme. StoreKit will simulate the products defined in the StoreKit file.
- Steps to test in the app:
  1. Use the app's paywall UI to attempt purchase of Basic or Generous support.
  2. Confirm the purchase flow completes.
  3. Use the app's "Restore purchases" action to verify restore flow.
- Verify app side effects:
  - iOS native provider stores flags in UserDefaults with the product key names (e.g., `support_basic`) and stores `<productId>_purchaseDate` in milliseconds. See `IosPurchaseProvider.swift`.
  - The Kotlin multiplatform `PurchaseManager.ios.kt` calls `storage.recordPurchase(...)` when purchase succeeds; this writes keys via multiplatform-settings (Kotlin) used across platforms. See `PurchaseStorage.kt`.

B. TestFlight / Sandbox testers (server receipts)
- Create sandbox testers in App Store Connect: Users and Access → Sandbox Testers.
- Upload a build to TestFlight and invite a tester (or add to internal testing).
- Install the TestFlight build on a device using a sandbox tester account.
- Perform purchases and test restore flows using the in‑app UI.

What to verify after a successful iOS purchase
- In app UI: premium/donation UI should reflect success (supporter state). The app calls `storage.recordPurchase(productId, PurchaseType.ONE_TIME_PURCHASE)` which sets:
  - `subscription_product_id` to the productId
  - `subscription_purchase_date` to a timestamp (ms)
  - `subscription_is_subscribed` to true
  - `subscription_purchase_type` to ONE_TIME_PURCHASE
  See `PurchaseStorage.kt`.
- You can also inspect UserDefaults on device for the presence of `support_basic` boolean and `support_basic_purchaseDate` (ms) — produced by the native `IosPurchaseProvider`.

Notes & gotchas (iOS)
- If you want to test actual App Store receipts and the real restore flow, use TestFlight + sandbox testers rather than StoreKit local config.
- Product ID mismatches are the most common cause of "product not found" errors — ensure the product ID in App Store Connect exactly matches `support_basic` / `support_generous`.

Part B — Google Play Console (Android)

Goal: Create two in‑app managed products (one‑time purchases) and test them with internal testing tracks.

Steps (Google Play Console)
1. Sign in to Google Play Console and open your app.
2. Go to Monetize → Products → In‑app products (or Monetize > Products > Add new product, depending on Play Console version).
3. Click "Create product" and enter:
   - Product ID: support_basic
   - Product Type: Managed product (one‑time in‑app purchase / INAPP)
   - Title / Description: Basic Support — GH₵ 10
   - Price: Set local price (GH₵ 10) and currency equivalents.
4. Save and activate the product.
5. Repeat for support_generous (product ID: support_generous, price GH₵ 20).
6. To test: upload an AAB to an Internal Test track (or Closed test) and add test accounts.
   - Go to Testing → Internal testing, create a release containing your signed AAB, and add tester emails.
   - For IAP testing, the app must be uploaded to a test track with the same package name and version code. The user must install the app from the Play Store internal test link for the billing flow to work.

Testing methods (Android)
- Use an internal test track or license test accounts (Play Console → Settings → Developer account → Account details → License testers) and install the app from the Play store internal testing link.
- Steps to test in the app:
  1. Use the app's paywall to buy Basic or Generous support.
  2. Confirm Google Play billing flow completes.
  3. Test "Restore purchases" action — in Android this is performed by calling `billingHelper.checkSubscriptionStatus()` which queries INAPP purchases via `queryPurchasesAsync(...)`. See `BillingHelper.kt`.
- Verify app side effects:
  - `BillingHelper.checkSubscriptionStatus` returns (hasPurchase, productId, purchaseTime). On success the Android `AndroidPurchaseManager` calls `storage.recordPurchase(productId, PurchaseType.ONE_TIME_PURCHASE, purchaseTimestamp)` if needed. See `PurchaseManager.android.kt` and `PurchaseStorage.kt`.

Notes & gotchas (Android)
- Google Play IAPs require the app to be uploaded to a test track for real purchases (or use Google Play Billing test purchases). Local debug builds installed via adb won't work unless they're signed and distributed via a testing track.
- Product ID must exactly match `support_basic` / `support_generous` (case sensitive).
- Make sure the app's package name and the uploaded AAB match the one configured in Play Console.
- For subscription products only: Android requires using subscriptionOfferDetails.offerToken and setOfferToken when launching billing flow. The current implementation treats both products as INAPPs; no offer token is required.

Verification & QA checklist (both platforms)

- [ ] Console: Create two products with IDs `support_basic` and `support_generous`.
- [ ] Platform product types: iOS Non‑Consumable, Android Managed Product / INAPP.
- [ ] Pricing: GH₵ 10 and GH₵ 20 (or equivalent USD tiers) set per store.
- [ ] App builds uploaded for testing: TestFlight (iOS) and Internal Test Track (Android) are prepared.
- [ ] Sandbox/test accounts set up: App Store sandbox testers and Play license testers.
- [ ] Purchase flows tested: purchase completes successfully.
- [ ] Restore flows tested: "Restore purchases" returns success and app records purchase timestamp.
- [ ] Data verification: In Kotlin `PurchaseStorage` the keys are set as:
  - `subscription_product_id` (string)
  - `subscription_purchase_date` (long millis)
  - `subscription_is_subscribed` (boolean)
  - `subscription_purchase_type` (string: ONE_TIME_PURCHASE)
- [ ] iOS UserDefaults inspected for `support_basic` or `support_generous` boolean and `<productId>_purchaseDate` key when using native provider.

How to test restore specifically

- iOS:
  - If using StoreKit local config, use the app's "Restore purchases" button which calls `SKPaymentQueue.restoreCompletedTransactions()`; `IosPurchaseProvider.paymentQueueRestoreCompletedTransactionsFinished` reads UserDefaults and returns success.
  - If using TestFlight/sandbox, sign in as sandbox tester and use the app's restore UI.
  - The Kotlin `IosPurchaseManager.restorePurchases` then reads `nativePurchaseProvider.getRestoredPurchasesInfo()` which returns a semicolon separated string of `productId,timestampMillis;...`.

- Android:
  - Install app from Play internal test track as a tester.
  - Use restore functionality in the app — Android's BillingClient `queryPurchasesAsync` will return active managed purchases; `BillingHelper.checkSubscriptionStatus` parses purchases and returns the latest productId and timestamp; `PurchaseManager.android.restorePurchases` records it in `PurchaseStorage`.

Troubleshooting (common problems)

- "Product not found" or product list empty:
  - Check product ID strings match exactly in store console and the app.
  - Ensure the IAP is activated in the console and not in a draft state.
  - On Android, ensure the AAB is uploaded to a test track and the tester installed the version from Play.

- Purchases failing with BillingResult codes (Android):
  - Inspect logs to get `billingResult.responseCode` and `debugMessage` from `BillingHelper`.
  - Ensure Google Play Services is available on the device/emulator.

- Restore returns false:
  - Ensure the tester account actually purchased the product in that store.
  - For iOS: if using StoreKit config, restore uses simulated transactions; use TestFlight for real receipts.

- Wrong product type (subscription vs non‑consumable):
  - If you incorrectly create the product as a subscription in Play or App Store, the app's purchase and restore code paths will not work as expected. The repo currently expects one‑time purchases (INAPP / NonConsumable).

Repository pointers to source of truth

- `composeApp/src/androidMain/kotlin/com/kobby/hymnal/core/iap/BillingHelper.kt` — Android billing implementation and constants SUPPORT_BASIC / SUPPORT_GENEROUS.
- `composeApp/src/androidMain/kotlin/com/kobby/hymnal/core/iap/PurchaseManager.android.kt` — Android purchase/restore mapping from PayPlan to product IDs.
- `composeApp/src/iosMain/kotlin/com/kobby/hymnal/core/iap/PurchaseManager.ios.kt` — iOS purchase/restore mapping and `getRestoredPurchasesInfo` parsing logic.
- `iosApp/iosApp/Core/iap/IosPurchaseProvider.swift` — Native provider for iOS, which writes UserDefaults flags and returns restored purchases string.
- `iosApp/iosApp/Core/iap/MyProducts.storekit` — StoreKit configuration file with `support_basic` and `support_generous` products (NonConsumable) used for local testing.
- `composeApp/src/commonMain/kotlin/com/kobby/hymnal/core/iap/PurchaseStorage.kt` — Persistent keys and `recordPurchase()` behavior.

Recommended next repo work (optional)

1. Add `docs/STORE_SETUP_GUIDE.md` (this file) to the repo (done).
2. Add a short section in the main `README.md` that references this guide.
3. Add a small developer script to clear purchase storage for speedier test runs (e.g., a debug menu or a Gradle task that clears multiplatform settings for a debug build).
4. Consider adding an automated smoke test script (Android instrumentation or small UI test) that runs through purchase/restore flows in the test track.

Questions / Assumptions

- Assumption 1: The app uses one‑time products (non‑consumable / managed inapp) — consistent with the code and StoreKit file.
- Assumption 2: You want a single consolidated guide in `docs/` for both stores; if you prefer separate per‑platform guides, let me know.

If you want, I can now:
- Create a short README addition linking to this guide, and/or
- Create a developer helper script to clear `PurchaseStorage` for testing, or
- Produce a small QA checklist as a GitHub issue template.
