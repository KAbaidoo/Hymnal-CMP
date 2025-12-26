# Subscription Module - Quick Reference

## 🚀 Quick Start

### Navigate to PayWall
```kotlin
navigator.push(PayWallScreen())
```

### Check if User is Subscribed
```kotlin
val subscriptionManager: SubscriptionManager = koinInject()
subscriptionManager.isUserSubscribed { isSubscribed ->
    // Handle subscription status
}
```

### Open Subscription Management
```kotlin
subscriptionManager.manageSubscription()
```

## 📦 Product IDs

### iOS (App Store Connect)
- `ios_yearly_subscription`
- `ios_onetime_subscription`

### Android (Google Play Console)
- `premium_subscription`

## 🏗️ Architecture

```
PayWallScreen → SubscriptionManager → Platform Implementation
     ↓                                        ↓
PayWallContent                    iOS: StoreKit / Android: Play Billing
```

## 📁 Key Files

### Common
- `SubscriptionManager.kt` - Interface
- `PayWallScreen.kt` - Screen wrapper
- `PayWall.kt` - UI composable

### iOS
- `SubscriptionManager.ios.kt` - iOS implementation
- `IosSubscriptionProvider.swift` - StoreKit integration
- `iOSApp.swift` - Provider initialization

### Android
- `SubscriptionManager.android.kt` - Android implementation
- `BillingHelper.kt` - Play Billing integration
- `SubscriptionModule.kt` - Koin module

## 🔧 Configuration

### iOS Initialization (iOSApp.swift)
```swift
SubscriptionManager_iosKt.initializeNativeSubscriptionProvider(
    provider: IosSubscriptionProvider()
)
```

### Android Initialization (MainActivity.kt)
```kotlin
startKoin {
    modules(... subscriptionModule)
}
```

## ✅ Build Status

- ✅ Android Debug Build: Successful
- ✅ iOS Simulator Build: Successful

## 📚 Documentation

1. **Integration Guide**: `SUBSCRIPTION_INTEGRATION.md`
2. **Testing Checklist**: `SUBSCRIPTION_TESTING_CHECKLIST.md`
3. **Usage Guide**: `SUBSCRIPTION_USAGE_GUIDE.md`
4. **Architecture**: `SUBSCRIPTION_ARCHITECTURE.md`
5. **Wiring Summary**: `SUBSCRIPTION_WIRING_SUMMARY.md`

## 🧪 Testing

### iOS
1. Configure products in App Store Connect
2. Create sandbox test account
3. Sign in with sandbox account on device
4. Test purchase flow

### Android
1. Configure subscription in Play Console
2. Upload to internal test track
3. Add test user email
4. Install from Play Store and test

## ⚙️ Next Steps

1. ☐ Configure product IDs in stores
2. ☐ Set up pricing
3. ☐ Create test accounts
4. ☐ Test purchase flow
5. ☐ Implement Privacy/Terms links
6. ☐ Add restore purchases
7. ☐ Implement analytics

## 🐛 Common Issues

### iOS
- **Products not loading**: Check product IDs match exactly
- **Sandbox account**: Sign out of App Store, sign in with sandbox

### Android
- **Billing not ready**: Ensure Play Services installed
- **Test purchases**: Must use internal/closed test track

## 💡 Tips

- Use sandbox/test accounts only for testing
- Check console logs for StoreKit/Billing events
- Verify product IDs are exact matches
- Test on real devices (not just emulators)

## 📞 Support Resources

- Apple StoreKit: https://developer.apple.com/storekit/
- Google Play Billing: https://developer.android.com/google/play/billing

---

**Status**: ✅ Ready for Testing  
**Last Updated**: December 26, 2025

