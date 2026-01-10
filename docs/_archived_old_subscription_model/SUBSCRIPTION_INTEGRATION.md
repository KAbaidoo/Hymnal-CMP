# Subscription Module Integration Guide

## Overview
This document outlines the integration of the subscription module for both iOS and Android platforms in the Hymnal-CMP application.

## Architecture

### Common Layer
- **SubscriptionManager Interface** (`commonMain/core/iap/SubscriptionManager.kt`)
  - `purchaseSubscription(plan: PayPlan, callback: (Boolean) -> Unit)`: Initiates purchase for selected plan
  - `isUserSubscribed(callback: (Boolean) -> Unit)`: Checks subscription status
  - `manageSubscription()`: Opens platform-specific subscription management

- **PayPlan Enum** (`commonMain/presentation/screens/settings/PayWall.kt`)
  - `Yearly`: Annual subscription
  - `OneTime`: One-time purchase

### iOS Implementation

#### Files Modified/Created:
1. **IosSubscriptionManager** (`iosMain/core/iap/SubscriptionManager.ios.kt`)
   - Maps PayPlan to product IDs:
     - `Yearly` → `yearly_subscription`
     - `OneTime` → `onetime_purchase`
   - Delegates to native Swift provider

2. **NativeSubscriptionProvider Interface** (`iosMain/core/iap/SubscriptionManager.ios.kt`)
   - Bridge between Kotlin and Swift
   - Methods:
     - `purchaseSubscription(productId: String, callback: (Boolean) -> Unit)`
     - `isUserSubscribed(callback: (Boolean) -> Unit)`
     - `fetchSubscriptions()`
     - `manageSubscription()`

3. **IosSubscriptionProvider.swift** (`iosApp/Core/iap/IosSubscriptionProvider.swift`)
   - Implements StoreKit integration
   - Supports both product IDs
   - Uses UserDefaults for subscription persistence
   - Handles transaction states

4. **iOSApp.swift** (`iosApp/iOSApp.swift`)
   - Initializes subscription provider on app launch:
   ```swift
   SubscriptionManager_iosKt.initializeNativeSubscriptionProvider(provider: IosSubscriptionProvider())
   ```

5. **MainViewController.kt** (`iosMain/MainViewController.kt`)
   - Loads `subscriptionModule` in Koin initialization

### Android Implementation

#### Files Modified/Created:
1. **AndroidSubscriptionManager** (`androidMain/core/iap/SubscriptionManager.android.kt`)
   - Accepts `PayPlan` parameter
   - Delegates to BillingHelper for Google Play Billing

2. **BillingHelper** (`androidMain/core/iap/BillingHelper.kt`)
   - Already implemented for Google Play Billing
   - Uses product ID: `premium_subscription`
   - Handles purchase flow and acknowledgment

3. **SubscriptionModule** (`androidMain/di/SubscriptionModule.kt`)
   - Provides `BillingHelper` and `AndroidSubscriptionManager` via Koin

4. **MainActivity.kt** (`androidMain/MainActivity.kt`)
   - Loads `subscriptionModule` in Koin initialization

### UI Layer

#### PayWallScreen (`commonMain/presentation/screens/settings/PayWallScreen.kt`)
- Voyager Screen wrapper for PayWall UI
- Manages purchase state:
  - `isProcessing`: Loading state during purchase
  - `purchaseError`: Error message on failure
- Passes selected plan to SubscriptionManager
- Handles navigation after successful purchase

#### PayWallContent (`commonMain/presentation/screens/settings/PayWall.kt`)
- Composable UI for paywall
- Accepts external `isLoading` and `errorMsg` states
- Provides plan selection UI
- Callbacks:
  - `onPurchase(plan: PayPlan)`: Triggered when user confirms purchase
  - `onBackClick()`: Navigation back
  - `onHomeClick()`: Navigation to home
  - `onPrivacy()`, `onTerms()`: Legal links (TODO)

## Product IDs Configuration

### iOS (App Store Connect)
Configure these product IDs in App Store Connect:
- `yearly_subscription` - Annual subscription (auto-renewable)
- `onetime_purchase` - One-time purchase (non-consumable)

### Android (Google Play Console)
Configure these product IDs in Google Play Console:
- `yearly_subscription` - Subscription product (ProductType.SUBS)
- `onetime_purchase` - One-time purchase product (ProductType.INAPP)

### Implementation Constants
Both platforms use the same product IDs defined as constants:
- **Common**: `PurchaseType` enum (YEARLY_SUBSCRIPTION, ONE_TIME_PURCHASE, NONE)
- **iOS**: `YEARLY_SUBSCRIPTION_ID`, `ONETIME_PURCHASE_ID` in `IosSubscriptionManager`
- **Android**: `YEARLY_SUBSCRIPTION`, `ONETIME_PURCHASE` in `BillingHelper`

## Usage

### Navigation to PayWall
```kotlin
navigator.push(PayWallScreen())
```

### Check Subscription Status
```kotlin
val subscriptionManager: SubscriptionManager = koinInject()

subscriptionManager.isUserSubscribed { isSubscribed ->
    if (isSubscribed) {
        // Grant premium features
    } else {
        // Show paywall
    }
}
```

### Manage Subscription
```kotlin
subscriptionManager.manageSubscription()
// Opens:
// - iOS: App Store subscription management
// - Android: Google Play subscription management
```

## Testing

### iOS Testing
1. Configure test products in App Store Connect
2. Use Sandbox test accounts
3. Test both product IDs
4. Verify transaction handling in Xcode console

### Android Testing
1. Configure test products in Google Play Console
2. Use test tracks (internal/closed testing)
3. Add test accounts
4. Verify purchase flow and acknowledgment

## Next Steps

1. **Configure Product IDs** in App Store Connect and Google Play Console
2. **Implement Privacy/Terms URLs** in PayWallScreen
3. **Add Analytics** to track purchase events
4. **Implement Receipt Validation** (optional but recommended)
5. **Add Restore Purchase** functionality
6. **Test thoroughly** with sandbox/test accounts
7. **Configure Auto-renewable vs One-time** products appropriately

## Notes

- Subscription state is currently persisted in UserDefaults (iOS) and via Google Play (Android)
- Consider implementing server-side receipt validation for production
- The current implementation supports basic purchase flow; enhance error handling as needed
- Both platforms now support plan selection (Yearly vs OneTime)

