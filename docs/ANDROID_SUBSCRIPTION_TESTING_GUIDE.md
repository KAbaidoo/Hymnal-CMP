# Android Subscription Manager - Quick Testing Guide

## ✅ Verification Checklist

### Build Status
- [x] **Gradle Build**: Successfully compiles without errors
- [x] **Error Handling**: All purchase flows have proper error callbacks
- [x] **Connection Management**: Play Store connection validated before operations
- [x] **Lifecycle Cleanup**: BillingHelper connection properly ended on activity destroy

## 🧪 Manual Testing Steps

### 1. Test Normal Purchase Flow
**Steps:**
1. Launch app and navigate to paywall (Settings → Upgrade)
2. Select a plan (Yearly or One-Time)
3. Tap "Continue" button
4. Complete purchase in Google Play dialog
5. Verify success callback navigates back

**Expected Result:**
- ✅ Purchase dialog appears
- ✅ After purchase, user is navigated back
- ✅ Subscription status updates

**Logs to Check:**
```
BillingHelper: checkSubscriptionStatus
BillingHelper: startConnection onBillingSetupFinished: 0
BillingHelper: BillingClient is ready
BillingHelper: Purchase listener triggered: 0
BillingHelper: Subscription is active: [premium_subscription]
BillingHelper: Purchase acknowledged
```

### 2. Test User Cancellation
**Steps:**
1. Launch app and navigate to paywall
2. Select a plan and tap "Continue"
3. Press back button in Google Play dialog (cancel purchase)

**Expected Result:**
- ✅ Error message appears: "Purchase failed. Please try again."
- ✅ User remains on paywall screen
- ✅ Can try again

**Logs to Check:**
```
BillingHelper: Purchase listener triggered: 1
BillingHelper: User canceled the purchase
```

### 3. Test Network Connection Failure
**Steps:**
1. Enable airplane mode
2. Launch app and navigate to paywall
3. Select a plan and tap "Continue"

**Expected Result:**
- ✅ Error message appears: "Purchase failed. Please try again."
- ✅ Processing state clears

**Logs to Check:**
```
BillingHelper: Failed to connect to Play Store
```

### 4. Test Already Owned Item
**Steps:**
1. Purchase subscription (if not already owned)
2. Try to purchase again

**Expected Result:**
- ✅ Purchase succeeds (callback returns true)
- ✅ User navigates back
- ✅ No duplicate charge

**Logs to Check:**
```
BillingHelper: Purchase listener triggered: 7
BillingHelper: Item already owned
```

### 5. Test Subscription Status Check
**Steps:**
1. Open app with active subscription
2. Navigate to Settings
3. Check if paywall is shown or hidden based on status

**Expected Result:**
- ✅ If subscribed: paywall should not be required
- ✅ If not subscribed: paywall appears

**Logs to Check:**
```
BillingHelper: checkSubscriptionStatus
BillingHelper: queryPurchasesAsync callback: 0, [count]
```

### 6. Test Lifecycle Cleanup
**Steps:**
1. Navigate to paywall
2. Start purchase flow
3. Press home button (app goes to background)
4. Force close app from recent apps

**Expected Result:**
- ✅ No memory leaks
- ✅ No crashes on app restart

**Logs to Check:**
```
BillingHelper: Ending billing client connection
```

## 🐛 Common Error Codes

| Code | Constant | Meaning | User Action |
|------|----------|---------|-------------|
| 0 | OK | Success | None |
| 1 | USER_CANCELED | User cancelled | Try again |
| 2 | SERVICE_UNAVAILABLE | Network issue | Check connection |
| 3 | BILLING_UNAVAILABLE | Play Store unavailable | Update Play Store |
| 4 | ITEM_UNAVAILABLE | Product not found | Contact support |
| 5 | DEVELOPER_ERROR | Invalid request | Contact support |
| 6 | ERROR | Fatal error | Try again later |
| 7 | ITEM_ALREADY_OWNED | Already purchased | Success |
| 8 | ITEM_NOT_OWNED | Not owned | Try purchasing |

## 📱 Testing Environments

### Development Testing (Debug Build)
```bash
# Build and install debug APK
./gradlew :composeApp:assembleDebug
adb install composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

### Google Play Testing
1. **License Testing**: Add test accounts in Play Console
2. **Internal Testing**: Upload to internal track
3. **Closed Testing**: Test with limited users

### Test Account Setup
1. Go to Play Console → Setup → License testing
2. Add test Google accounts
3. Configure test responses (PURCHASED, CANCELED, etc.)

## 🔍 Debugging Tips

### Enable Verbose Logging
```kotlin
// In BillingHelper, all operations now log with TAG
// Filter logs by:
adb logcat -s BillingHelper
```

### Check Purchase State
```kotlin
// In Android Studio Logcat, filter by:
"BillingHelper"
```

### Common Issues

**Issue**: Purchase dialog doesn't appear
- Check: Network connection
- Check: Play Store app updated
- Check: Product ID exists in Play Console

**Issue**: "Item not available" error
- Check: App signed with correct certificate
- Check: Product configured in Play Console
- Check: App published to at least internal track

**Issue**: Purchase completes but callback not called
- Check: `purchaseCallback` not null
- Check: Acknowledgement successful
- Check: Purchase state is PURCHASED not PENDING

## 📊 Success Criteria

### All Tests Pass ✅
- [ ] Normal purchase completes successfully
- [ ] User cancellation handled gracefully
- [ ] Network errors show proper message
- [ ] Already owned items handled correctly
- [ ] Subscription status check works
- [ ] No memory leaks on activity destruction
- [ ] App doesn't crash in any scenario
- [ ] Proper logging for all operations

### Production Ready 🚀
When all checkboxes are complete:
1. Test with real payment in internal track
2. Verify subscription in Play Console
3. Test subscription cancellation
4. Verify grace period handling
5. Deploy to production

## 🔗 Related Files
- `BillingHelper.kt` - Core billing logic
- `AndroidSubscriptionManager.kt` - Platform implementation
- `PayWallScreen.kt` - UI implementation
- `MainActivity.kt` - Lifecycle management

## 📚 Documentation
- [Android Subscription Wiring Fix](ANDROID_SUBSCRIPTION_WIRING_FIX.md)
- [Subscription Architecture](SUBSCRIPTION_ARCHITECTURE.md)
- [Subscription Testing Checklist](SUBSCRIPTION_TESTING_CHECKLIST.md)

