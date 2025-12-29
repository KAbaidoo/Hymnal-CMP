# Trial Period Implementation Guide

## Overview

The Hymnal app provides a **30-day free trial** for all new users. This trial period allows users to access premium features before deciding to purchase a subscription.

## How It Works

### First Install Detection

When a user installs the app for the first time:
1. The `SubscriptionStorage.initializeFirstInstallIfNeeded()` is called
2. Current timestamp is stored as `firstInstallDate` in persistent storage
3. This date remains unchanged even if the app is reinstalled

### Trial Period Calculation

```kotlin
val daysSinceInstall = (currentTime - firstInstallDate) / MILLIS_PER_DAY
val daysRemaining = TRIAL_DURATION_DAYS - daysSinceInstall
```

- **Trial Duration**: 30 days
- **Days Remaining**: Calculated in real-time based on install date
- **Expiration**: When daysRemaining reaches 0

### Entitlement States

The system uses five distinct states:

| State | Description | Has Access | Shows Paywall |
|-------|-------------|-----------|---------------|
| `TRIAL` | Within 30-day trial period | ✅ Yes | ❌ No |
| `SUBSCRIBED` | Active subscription or purchase | ✅ Yes | ❌ No |
| `TRIAL_EXPIRED` | Trial ended, no purchase | ❌ No | ✅ Yes |
| `SUBSCRIPTION_EXPIRED` | Renewable subscription expired | ❌ No | ✅ Yes |
| `NONE` | Fresh install, no trial started | ❌ No | ✅ Yes |

## Implementation Details

### Storage Keys

```kotlin
KEY_FIRST_INSTALL_DATE = "subscription_first_install_date"  // Long timestamp
KEY_PURCHASE_DATE = "subscription_purchase_date"           // Long timestamp
KEY_PURCHASE_TYPE = "subscription_purchase_type"           // String enum
KEY_PRODUCT_ID = "subscription_product_id"                 // String
KEY_EXPIRATION_DATE = "subscription_expiration_date"       // Long timestamp
KEY_IS_SUBSCRIBED = "subscription_is_subscribed"           // Boolean
KEY_LAST_VERIFICATION_TIME = "subscription_last_verification_time" // Long timestamp
```

### Initialization Flow

```kotlin
// On app startup (in MainActivity or App initialization)
val subscriptionManager: SubscriptionManager = koinInject()
subscriptionManager.initialize()

// This will:
// 1. Set firstInstallDate if not already set
// 2. Check subscription status with platform (Google/Apple)
// 3. Update entitlement state
// 4. Emit new state via StateFlow
```

### Checking Trial Status

```kotlin
val storage: SubscriptionStorage = koinInject()

// Get days remaining
val daysRemaining = storage.getTrialDaysRemaining() // Returns Int? (null if subscribed)

// Check if trial is active
val isActive = storage.isTrialActive() // Returns Boolean

// Get complete entitlement info
val info = storage.getEntitlementInfo()
println("State: ${info.state}")
println("Days remaining: ${info.trialDaysRemaining}")
println("Has access: ${info.hasAccess}")
```

### UI Integration

#### Display Trial Info in PayWall

```kotlin
val entitlementInfo by subscriptionManager.entitlementState.collectAsState()

PayWallContent(
    trialDaysRemaining = entitlementInfo.trialDaysRemaining,
    // ... other parameters
)
```

The PayWall header automatically shows:
```
"5 days left in trial"
```

#### Feature Gating

```kotlin
// Simple gating - shows paywall if no access
PremiumFeatureGate(
    premiumContent = {
        // Your premium feature UI
        PremiumHymnDetailsScreen()
    }
)

// Advanced - custom behavior
CheckPremiumAccess(
    onHasAccess = { entitlementInfo ->
        if (entitlementInfo.isInTrial) {
            TrialBanner("${entitlementInfo.trialDaysRemaining} days left")
        }
        PremiumContent()
    },
    onNoAccess = { entitlementInfo ->
        FreeContent()
        UpgradeButton(onClick = { navigator.push(PayWallScreen()) })
    }
)
```

## Edge Cases Handled

### 1. Device Clock Changes

**Problem**: User changes device time to extend trial

**Solution**:
- All calculations use `System.currentTimeMillis()`
- Future timestamps are validated against `lastVerificationTime`
- Platform verification (Google/Apple) provides authoritative state

```kotlin
// The system validates that current time is reasonable
if (currentTime < firstInstallDate) {
    // Clock was moved backward - use last known good state
    // Or re-verify with platform
}
```

### 2. Reinstall After Trial

**Scenario**: User uninstalls app after trial expires, then reinstalls

**Behavior**:
- `firstInstallDate` persists in platform storage (SharedPreferences/UserDefaults)
- On reinstall, trial is already expired
- User must purchase to access premium features
- **Restore Purchases** button helps recover prior purchases

**Code**:
```kotlin
// On reinstall, storage loads existing firstInstallDate
storage.initializeFirstInstallIfNeeded() // Does nothing if already set
val state = storage.getEntitlementState() // Returns TRIAL_EXPIRED
```

### 3. Multiple Devices

**Scenario**: User installs on phone, then on tablet

**Behavior**:
- Each device has its own trial period
- Purchases are synced via platform (Google Play/App Store)
- Calling `restorePurchases()` syncs purchased entitlements

**Platform Behavior**:
- **Google Play**: Automatic sync via `queryPurchasesAsync`
- **App Store**: Manual restore via `restoreCompletedTransactions`

### 4. Offline Usage

**Scenario**: User has no internet connection

**Behavior**:
- Trial calculation works offline (based on device time)
- Subscription status uses last cached state
- `lastVerificationTime` tracks when platform was last queried
- On reconnection, state is refreshed

**Code**:
```kotlin
// Offline: Uses cached data
val info = storage.getEntitlementInfo()
if (info.hasAccess) {
    // Allow access based on cached state
}

// Online: Refresh from platform
subscriptionManager.isUserSubscribed { isSubscribed ->
    // Updates cache with fresh data
}
```

### 5. Grace Period After Trial Expiry

**Optional Enhancement** (not currently implemented):

```kotlin
// Add 3-day grace period after trial
fun getTrialDaysRemaining(): Int? {
    // ... existing code ...
    val daysRemaining = TRIAL_DURATION_DAYS - daysSinceInstall.toInt()
    val withGracePeriod = daysRemaining + GRACE_PERIOD_DAYS
    return if (withGracePeriod > 0) withGracePeriod else 0
}
```

## Testing

### Manual Testing Scenarios

1. **Fresh Install**
   ```
   - Install app
   - Verify trial shows "30 days left"
   - Verify all features accessible
   ```

2. **Mid-Trial**
   ```
   - Set firstInstallDate to 15 days ago
   - Restart app
   - Verify shows "15 days left"
   ```

3. **Trial Expiry**
   ```
   - Set firstInstallDate to 31 days ago
   - Restart app
   - Verify paywall appears
   - Verify features are locked
   ```

4. **Purchase During Trial**
   ```
   - Start with active trial
   - Complete purchase
   - Verify trial info disappears
   - Verify "SUBSCRIBED" state
   ```

5. **Restore After Reinstall**
   ```
   - Have active subscription
   - Uninstall app
   - Reinstall app
   - Click "Restore Purchases"
   - Verify subscription restored
   ```

### Automated Tests

See `SubscriptionStorageTest.kt` for comprehensive unit tests covering:
- Trial period calculation
- Entitlement state transitions
- Storage persistence
- Edge cases

## Constants

```kotlin
TRIAL_DURATION_DAYS = 30              // Trial length
MILLIS_PER_DAY = 24 * 60 * 60 * 1000L // Time conversion
```

To modify trial length, change `TRIAL_DURATION_DAYS` in `SubscriptionStorage.kt`.

## Troubleshooting

### Trial Not Starting

**Check**:
```kotlin
val storage: SubscriptionStorage = koinInject()
println("First install: ${storage.firstInstallDate}")
println("Is initialized: ${storage.firstInstallDate > 0}")
```

**Fix**:
```kotlin
storage.initializeFirstInstallIfNeeded()
```

### Trial Shows Wrong Days

**Check**:
```kotlin
val currentTime = System.currentTimeMillis()
val daysSince = (currentTime - storage.firstInstallDate) / MILLIS_PER_DAY
println("Days since install: $daysSince")
```

**Possible causes**:
- Device clock changed
- firstInstallDate corrupted

**Fix**:
```kotlin
// For testing only - reset trial
storage.clearAll()
storage.initializeFirstInstallIfNeeded()
```

### User Stuck in Trial After Purchase

**Check**:
```kotlin
println("Is subscribed: ${storage.isSubscribed}")
println("Purchase type: ${storage.purchaseType}")
```

**Fix**:
```kotlin
// Manually verify purchase
subscriptionManager.isUserSubscribed { isSubscribed ->
    println("Platform says subscribed: $isSubscribed")
}
```

## Best Practices

1. **Always initialize on startup**
   ```kotlin
   subscriptionManager.initialize()
   ```

2. **Use reactive state for UI**
   ```kotlin
   val entitlementInfo by subscriptionManager.entitlementState.collectAsState()
   ```

3. **Verify periodically**
   ```kotlin
   // Check with platform every app launch
   subscriptionManager.isUserSubscribed { /* update UI */ }
   ```

4. **Handle restore gracefully**
   ```kotlin
   subscriptionManager.restorePurchases { success ->
       if (success) {
           showMessage("Purchases restored!")
       } else {
           showMessage("No purchases found")
       }
   }
   ```

5. **Test edge cases**
   - Fresh install
   - Reinstall after trial
   - Reinstall with active subscription
   - Offline usage
   - Clock changes
