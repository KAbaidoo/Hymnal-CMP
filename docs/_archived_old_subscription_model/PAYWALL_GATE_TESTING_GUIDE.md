# PayWall Gate - Quick Testing Guide

## ✅ Implementation Complete

The PayWall gate has been successfully implemented. Here's how to test it:

## Quick Test Scenarios

### 1. Fresh Install (Trial Period)
**Expected Behavior:**
- ✅ App launches → StartScreen appears (splash with random hymn)
- ✅ After 6 seconds → Auto-navigate to HomeScreen
- ✅ Trial banner visible at top: "7 days left in trial | Upgrade Now"
- ✅ All features accessible (hymns, favorites, search, etc.)
- ✅ Click trial banner → Navigate to PayWall

**How to Test:**
```bash
# Android - Clear app data
adb shell pm clear com.kobby.hymnal

# iOS - Delete app and reinstall
```

### 2. Trial Expiration Simulation
**Expected Behavior:**
- ❌ App launches → Immediately shows PayWall (skips StartScreen)
- ❌ Cannot access HomeScreen or any features
- ❌ "Maybe Later" button still shows PayWall
- ✅ Must purchase to continue

**How to Test:**
You can't easily simulate this without modifying the trial duration. Options:
1. **Change trial duration** (for testing only):
   - Edit `SubscriptionStorage.kt` → Change `TRIAL_DURATION_DAYS = 7` to `0`
   - Rebuild app
   - Clear app data and launch

2. **Modify device date** (may not work due to validation):
   - Set device date forward by 8+ days
   - Launch app (may be detected as invalid)

3. **Wait 7 days** (production testing)

### 3. After Purchase
**Expected Behavior:**
- ✅ App launches → Normal StartScreen → HomeScreen flow
- ✅ NO trial banner (user is subscribed)
- ✅ All features accessible
- ✅ Reinstall → Purchase restored, access maintained

**How to Test:**
```bash
# Use test subscription product IDs
# Follow platform-specific test purchase flows
# - Android: Google Play test tracks
# - iOS: StoreKit configuration files
```

## Key Files Modified

### 1. MainActivity.kt (Android)
```kotlin
// Added subscription initialization
val subscriptionManager: SubscriptionManager by inject()
subscriptionManager.initialize()
```

### 2. MainViewController.kt (iOS)
```kotlin
// Added subscription initialization
object : KoinComponent {
    init {
        val subscriptionManager: SubscriptionManager by inject()
        subscriptionManager.initialize()
    }
}
```

### 3. StartScreen.kt
```kotlin
// Wrapped entire app with PremiumFeatureGate
PremiumFeatureGate(
    premiumContent = {
        // StartScreen content + navigation
    },
    showPaywallOnDenied = true
)
```

### 4. HomeScreen.kt
```kotlin
// Added trial banner
if (entitlementInfo.isInTrial) {
    TrialBanner(
        daysRemaining = entitlementInfo.trialDaysRemaining ?: 0,
        onUpgradeClick = { navigator.push(PayWallScreen()) }
    )
}
```

## Visual Verification

### During Trial
```
┌─────────────────────────────────────┐
│  StartScreen (Splash)               │
│  - Anglican Hymnal logo             │
│  - Random hymn preview              │
│  - Arrow button to continue         │
│  - Auto-navigate after 6s           │
└─────────────────────────────────────┘
                ↓
┌─────────────────────────────────────┐
│  HomeScreen                         │
│  ┌─────────────────────────────┐   │
│  │ 2 days left | Upgrade Now   │ ← TRIAL BANNER
│  └─────────────────────────────┘   │
│  Find your hymns card               │
│  - Ancient & Modern                 │
│  - Supplementary                    │
│  - Canticles                        │
│  - The Psalms                       │
└─────────────────────────────────────┘
```

### After Trial Expiration
```
┌─────────────────────────────────────┐
│  PayWallScreen                      │
│  - Unlock Full Anglican Hymnal      │
│  - USD 0.99/year option             │
│  - USD 2.50 one-time option         │
│  - Continue button                  │
│  - Restore purchases                │
│  - Maybe later (stays on paywall)   │
└─────────────────────────────────────┘
     ↑
     └── No access to HomeScreen or features
```

## Entitlement States Reference

| State | hasAccess | Shows Paywall | Trial Banner |
|-------|-----------|---------------|--------------|
| `TRIAL` | ✅ Yes | ❌ No | ✅ Shows countdown |
| `SUBSCRIBED` | ✅ Yes | ❌ No | ❌ Hidden |
| `TRIAL_EXPIRED` | ❌ No | ✅ Yes | ❌ N/A |
| `SUBSCRIPTION_EXPIRED` | ❌ No | ✅ Yes | ❌ N/A |
| `NONE` | ❌ No | ✅ Yes | ❌ N/A |

## Debug Logging

To verify subscription state during testing, check logs:

### Android
```bash
adb logcat | grep -i "subscription\|entitlement\|trial"
```

### iOS
```bash
# In Xcode console, filter for:
# "subscription", "entitlement", "trial"
```

## Troubleshooting

### Issue: Trial banner doesn't show
**Check:**
- Is `entitlementInfo.isInTrial` true?
- Is trial period active (< 7 days since install)?
- Is `firstInstallDate` set in storage?

### Issue: PayWall doesn't appear after trial
**Check:**
- Is `SubscriptionManager.initialize()` called on app start?
- Is `PremiumFeatureGate` correctly wrapping StartScreen content?
- Check entitlement state in logs

### Issue: Can still access features after trial
**Check:**
- Is gate properly implemented in StartScreen?
- Is subscription state cached incorrectly?
- Clear app data and retry

## Next Steps

1. **Test on physical device** (not just emulator)
2. **Verify platform subscription integration** (Google Play/App Store)
3. **Test restore purchases flow**
4. **Monitor Crashlytics** for any subscription-related errors
5. **A/B test** different paywall timing strategies

## Documentation

For more details, see:
- `PAYWALL_GATE_IMPLEMENTATION.md` - Full implementation details
- `TRIAL_PERIOD_GUIDE.md` - How trial period works
- `FEATURE_GATING_USAGE_GUIDE.md` - Using PremiumFeatureGate in other screens
- `PAYWALL_README.md` - PayWall system overview

---

**Status**: ✅ Ready for testing  
**Last Updated**: January 4, 2026
