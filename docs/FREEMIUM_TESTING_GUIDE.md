# Testing Guide: Generous Freemium Model

## Quick Test Scenarios

### Scenario 1: New User Journey (Free Experience)
**Goal**: Verify free features work without interruption

1. Fresh install the app
2. Browse all hymn categories - should work freely
3. Read 9 hymns - no prompts should appear
4. Search for hymns - should work freely
5. View "Hymn of the Day" - should work freely
6. **Expected**: No restrictions, full access to core worship features

### Scenario 2: 10th Hymn Trigger
**Goal**: Verify support sheet appears at natural interruption point

1. Fresh install the app
2. Read 10 different hymns
3. **Expected**: After 10th hymn, support sheet appears
4. Close the sheet (tap X or back)
5. **Expected**: Can continue reading hymns freely
6. Navigate to 11th, 12th hymn
7. **Expected**: No more prompts (only triggers once)

### Scenario 3: Premium Feature Access (Favorites)
**Goal**: Verify favorites feature is gated

1. Fresh install or clear app data
2. Read a hymn
3. Tap the "Favorites" (heart) button
4. **Expected**: Support sheet appears immediately
5. Close the sheet
6. Navigate to More → Favorites
7. **Expected**: Support sheet appears immediately

### Scenario 4: Premium Feature Access (Font Settings)
**Goal**: Verify font customization is gated

1. Fresh install or clear app data
2. Read a hymn
3. Tap the "Font Settings" (Aa) button
4. **Expected**: Support sheet appears immediately
5. Close the sheet
6. **Expected**: Font settings modal does NOT appear

### Scenario 5: Premium Feature Access (Highlights)
**Goal**: Verify highlights feature is gated

1. Fresh install or clear app data
2. Navigate to More → Highlights
3. **Expected**: Support sheet appears immediately
4. Close the sheet
5. **Expected**: Returns to previous screen

### Scenario 6: Support via More Menu
**Goal**: Verify easy access to support

1. Navigate to More screen (three dots icon)
2. **Expected**: "Support Development" is first item in list
3. Tap "Support Development"
4. **Expected**: Support sheet appears
5. View pricing options: GH₵ 15 and GH₵ 20
6. **Expected**: Both options visible, GH₵ 15 selected by default

### Scenario 7: Complete Purchase (Basic Tier)
**Goal**: Verify all features unlock after support

1. Fresh install or clear app data
2. Navigate to More → Support Development
3. Select GH₵ 15 option
4. Tap "Continue"
5. Complete purchase flow
6. **Expected**: Returns to app
7. Tap favorites button on hymn
8. **Expected**: Adds to favorites (no prompt)
9. Tap font settings button
10. **Expected**: Font settings modal appears
11. Navigate to More → Highlights
12. **Expected**: Highlights screen appears (no prompt)
13. Read 20 more hymns
14. **Expected**: No support prompts appear

### Scenario 8: Complete Purchase (Generous Tier)
**Goal**: Verify same features for both tiers

1. Fresh install or clear app data
2. Navigate to More → Support Development  
3. Select GH₵ 20 option (with "Generous" badge)
4. Tap "Continue"
5. Complete purchase flow
6. **Expected**: Same behavior as Scenario 7
7. All premium features unlock identically

### Scenario 9: Restore Purchases
**Goal**: Verify restoration after reinstall

1. Have previously purchased support
2. Uninstall app
3. Reinstall app
4. Tap favorites button
5. **Expected**: Support sheet appears
6. Tap "Restore Purchases"
7. **Expected**: "Purchases restored successfully!" message
8. Tap favorites button again
9. **Expected**: Adds to favorites (no prompt)

### Scenario 10: Support Sheet UI/UX
**Goal**: Verify messaging and pricing display

1. Trigger support sheet (any method)
2. **Verify Header**:
   - Title: "Enjoying the Hymnal?"
   - Subtitle mentions "passion project" and "keep our hymns alive"
3. **Verify Pricing**:
   - First option: "GH₵ 15 / One-time"
   - Second option: "GH₵ 20 / One-time" with "Generous" badge
   - Both show same subtitle about unlocking features
4. **Verify Features List**:
   - Favorites & bookmarks
   - Text highlighting
   - Font customization
   - (NOT listed: Full hymn library, Offline access, No ads)
5. **Verify Support Message**:
   - "Support this Ministry" heading
   - Mentions MTN MoMo or Telecel Cash
6. **Verify Buttons**:
   - "Continue" button (primary)
   - "Restore Purchases" button (secondary)
   - Close (X) button in header (if dismissible)

## Edge Cases to Test

### Edge Case 1: Rapid Feature Access
1. Fresh install
2. Quickly tap: Favorites → close → Font Settings → close → More/Highlights
3. **Expected**: Support sheet appears for each, counter increments

### Edge Case 2: Background/Foreground
1. Read 5 hymns
2. Put app in background
3. Return to app
4. Read 5 more hymns
5. **Expected**: Support sheet appears on 10th

### Edge Case 3: Network Loss During Purchase
1. Disable network
2. Attempt purchase
3. **Expected**: Error message appears
4. Re-enable network
5. Retry purchase
6. **Expected**: Purchase completes successfully

### Edge Case 4: Multiple Devices (Same Account)
1. Purchase on Device A
2. Install on Device B (same account)
3. Tap "Restore Purchases"
4. **Expected**: Features unlock on Device B

## Test Data Reset

### Clear All Data (Android)
```bash
adb shell pm clear com.kobby.hymnal
```

### Clear All Data (iOS)
1. Long press app icon
2. Remove App
3. Reinstall from App Store

### Manual Storage Reset (For Testing)
If you have developer access, you can call:
```kotlin
val storage: SubscriptionStorage = koinInject()
storage.clearAll() // Resets trial, purchase, and usage data
```

## Success Criteria

✅ **Core worship features always free** (hymn reading, basic search, categories)  
✅ **10-hymn prompt triggers once** and is dismissible  
✅ **Premium features gated consistently** (favorites, highlights, fonts)  
✅ **Support sheet messaging** culturally appropriate for Ghana  
✅ **Two-tier pricing** displays correctly (GH₵ 15 & GH₵ 20)  
✅ **All features unlock** after either purchase  
✅ **Restore purchases** works after reinstall  
✅ **No prompts after support** - seamless premium experience  
✅ **Easy access** to support via More menu  

## Automated Test Ideas

### Unit Tests
- `UsageTrackingManager.recordHymnRead()` returns true on 10th hymn
- `EntitlementInfo.canAccessFeature()` checks purchase status
- `SubscriptionStorage.hymnsReadCount` persists across sessions

### Integration Tests
- Navigate to Favorites → expect support sheet
- Purchase support → verify hymn counter resets
- Read 10 hymns → verify support sheet appears

### UI Tests
- Verify support sheet UI elements present
- Verify pricing displays correctly
- Verify feature list shows only premium features

---

**Last Updated**: January 6, 2026  
**Model**: Generous Freemium  
**Target Market**: Ghana

