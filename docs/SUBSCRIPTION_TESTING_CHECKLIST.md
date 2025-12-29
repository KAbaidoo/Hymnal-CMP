# Subscription Module Testing Checklist

## Pre-Testing Setup

### iOS (App Store Connect)
- [ ] Create App Store Connect account and app listing
- [ ] Configure In-App Purchases:
  - [ ] Product ID: `ios_yearly_subscription` (Auto-renewable subscription)
  - [ ] Product ID: `ios_onetime_purchase` (Non-consumable or consumable)
- [ ] Set up pricing for both products
- [ ] Create sandbox test user accounts
- [ ] Enable StoreKit Configuration file for local testing (optional)

### Android (Google Play Console)
- [ ] Create Google Play Console account and app listing
- [ ] Configure In-App Products:
  - [ ] Product ID: `premium_subscription` (Subscription)
- [ ] Set up pricing for the product
- [ ] Configure subscription options (billing period, trial, etc.)
- [ ] Create test tracks (Internal Testing recommended)
- [ ] Add test user emails to license testing

## iOS Testing

### Local Testing (Xcode)
- [ ] Open project in Xcode
- [ ] Build and run on simulator/device
- [ ] Verify products are fetched on app launch (check console logs)
- [ ] Navigate to PayWall screen
- [ ] Verify both plans are displayed
- [ ] Test purchase flow with sandbox account:
  - [ ] Select Yearly plan and purchase
  - [ ] Verify success callback
  - [ ] Check UserDefaults for subscription flag
  - [ ] Test OneTime plan purchase
- [ ] Test subscription status check
- [ ] Test manage subscription button

### Error Scenarios
- [ ] Test with invalid product ID
- [ ] Test purchase cancellation
- [ ] Test with no internet connection
- [ ] Test with already purchased product

## Android Testing

### Local Testing (Android Studio)
- [ ] Build and run on emulator/device
- [ ] Navigate to PayWall screen
- [ ] Verify both plans are displayed
- [ ] Test purchase flow:
  - [ ] Select plan and initiate purchase
  - [ ] Complete purchase in Google Play test flow
  - [ ] Verify success callback
  - [ ] Check subscription status
- [ ] Test manage subscription button
- [ ] Verify purchase acknowledgment in logs

### Play Store Testing
- [ ] Upload to internal test track
- [ ] Install via Play Store on test device
- [ ] Test complete purchase flow
- [ ] Verify subscription appears in Google Play account

### Error Scenarios
- [ ] Test purchase cancellation
- [ ] Test with no internet connection
- [ ] Test billing client connection failures
- [ ] Test already owned subscription

## Functional Testing

### Navigation
- [ ] Verify PayWall can be opened from appropriate screens
- [ ] Test back button behavior
- [ ] Test home button behavior
- [ ] Verify navigation after successful purchase

### UI/UX
- [ ] Verify loading state during purchase
- [ ] Verify error messages display correctly
- [ ] Test plan selection (radio buttons)
- [ ] Verify Privacy and Terms links (once implemented)
- [ ] Test on different screen sizes
- [ ] Test in light and dark modes

### State Management
- [ ] Verify isProcessing state prevents multiple purchases
- [ ] Verify error state clears on retry
- [ ] Test subscription state persistence across app restarts

## Edge Cases

### iOS
- [ ] Test with expired subscription (after cancellation)
- [ ] Test with multiple subscriptions active
- [ ] Test StoreKit transaction observer registration
- [ ] Test app kill during purchase
- [ ] Test with VPN/different regions

### Android
- [ ] Test with Play Store not installed/disabled
- [ ] Test billing client lifecycle
- [ ] Test pending purchases
- [ ] Test app kill during purchase
- [ ] Test with different Play Store accounts

## Integration Testing

- [ ] Test subscription check on app launch
- [ ] Test premium feature unlocking based on subscription
- [ ] Test subscription expiry handling
- [ ] Test restore purchases (when implemented)

## Performance Testing

- [ ] Monitor memory usage during purchase flow
- [ ] Check for memory leaks after multiple purchases
- [ ] Verify no ANR/crashes on Android
- [ ] Verify no freezing on iOS

## Analytics (When Implemented)

- [ ] Verify purchase events are tracked
- [ ] Verify error events are logged
- [ ] Verify Crashlytics integration works

## Production Readiness

### iOS
- [ ] Submit app for review with in-app purchases
- [ ] Test on production with real purchase (small test)
- [ ] Verify receipt validation (if implemented)
- [ ] Set up server-side validation (recommended)

### Android
- [ ] Submit app for review
- [ ] Promote to production track
- [ ] Test on production with real purchase (small test)
- [ ] Verify receipt validation (if implemented)
- [ ] Set up server-side validation (recommended)

## Documentation

- [ ] Document product IDs in team wiki
- [ ] Document test accounts for team
- [ ] Create user guide for subscription features
- [ ] Document troubleshooting steps for common issues

## Notes

- Use sandbox/test accounts only for testing
- Never use real payment methods for testing
- Keep track of test account credentials securely
- Monitor crash reports during testing phase
- Consider implementing restore purchases before production

