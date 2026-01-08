# Free App with Donation Prompts - Implementation Plan

**Date Created**: January 8, 2026  
**Status**: In Progress  
**Previous Model**: Generous Freemium with Feature Gates  
**New Model**: Fully Free with Exponential Backoff Donation Prompts

---

## Executive Summary

Transform the Anglican Hymnal app from a generous freemium model (with feature gates) to a completely free experience. All features—favorites, highlights, and font customization—will be accessible to everyone. Users will be gently encouraged to support development through donation prompts that appear less frequently over time using exponential backoff. Supporters receive relief from prompts for approximately one year, after which a very gentle reminder appears.

### Core Philosophy Change

**OLD**: "Worship is Free, Tools are Premium"  
**NEW**: "Everything is Free, Support is Appreciated"

---

## Implementation Status Tracker

### Phase 1: Core Architecture Changes ✅ COMPLETED
- [x] Updated `EntitlementState.kt` - Removed `PremiumFeature` enum, renamed `SUBSCRIBED` to `SUPPORTED`
- [x] Updated `PurchaseStorage.kt` - Added donation tracking fields and exponential backoff logic
- [x] Updated `UsageTrackingManager.kt` - Implemented exponential backoff algorithm
- [ ] Update platform-specific `PurchaseManager` implementations (Android/iOS)

### Phase 2: Remove Feature Gates ✅ COMPLETED
- [x] Updated `HymnDetailScreen.kt` - Removed feature gates, updated donation prompt logic
- [x] Updated `FavoritesScreen.kt` - Removed `PremiumFeatureAccess` wrapper
- [x] Updated `HighlightsScreen.kt` - Removed `PremiumFeatureAccess` wrapper

### Phase 3: Update UI & Messaging 🚧 IN PROGRESS
- [x] Added `isYearlyReminder` parameter to `PayWallScreen.kt`
- [x] Added `isYearlyReminder` parameter to `PayWallContent` in `PayWall.kt`
- [ ] Update `PaywallHeader` to show conditional messaging
- [ ] Update donation prompt copy throughout `PayWall.kt`
- [ ] Update string resources for new messaging

### Phase 4: Delete Obsolete Files ⏳ PENDING
- [ ] Delete `PremiumFeatureGate.kt`
- [ ] Delete `SupportSheetTrigger.kt`

### Phase 5: Testing & Validation ⏳ PENDING
- [ ] Build and fix compilation errors
- [ ] Manual testing of exponential backoff
- [ ] Test yearly reminder for supporters
- [ ] Verify all features accessible without gates

### Phase 6: Documentation ⏳ PENDING
- [ ] Update `IMPLEMENTATION_REVIEW_JAN_2026.md`
- [ ] Archive old freemium docs
- [ ] Update `README.md`

---

## Detailed Implementation Guide

### PHASE 1: Core Architecture Changes ✅ COMPLETED

#### 1.1 EntitlementState.kt ✅ DONE
**File**: `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/core/iap/EntitlementState.kt`

**Changes Made**:
- ✅ Removed `PremiumFeature` enum entirely (FAVORITES, HIGHLIGHTS, FONT_CUSTOMIZATION)
- ✅ Renamed `EntitlementState.SUBSCRIBED` to `EntitlementState.SUPPORTED`
- ✅ Updated `EntitlementInfo.hasAccess` to always return `true` (deprecated property)
- ✅ Removed `canAccessFeature()` method
- ✅ Updated `hasSupported` property logic

**Result**: All feature access checks are now removed. The enum only tracks supporter status for donation prompt management.

---

#### 1.2 PurchaseStorage.kt ✅ DONE
**File**: `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/core/iap/PurchaseStorage.kt`

**Changes Made**:
- ✅ Added new storage keys:
  - `KEY_DONATION_PROMPT_COUNT`
  - `KEY_LAST_DONATION_PROMPT_TIMESTAMP`
  - `KEY_LAST_DONATION_DATE`
  - `KEY_NEXT_PROMPT_THRESHOLD`
  - `KEY_HYMNS_SINCE_DONATION`

- ✅ Added new properties:
  - `donationPromptCount: Int`
  - `lastDonationPromptTimestamp: Long?`
  - `lastDonationDate: Long?`
  - `nextPromptThreshold: Int` (default: 10)
  - `hymnsSinceDonation: Int`

- ✅ Added new methods:
  - `shouldShowYearlyReminder(): Boolean` - Checks if 365 days passed
  - `recordDonation()` - Resets counters, sets donation date
  - `calculateNextThreshold(isSupporter: Boolean): Int` - Exponential backoff calculation

- ✅ Removed deprecated methods:
  - `getFeatureAccessAttempts(feature: PremiumFeature)`
  - `setFeatureAccessAttempts(feature: PremiumFeature, count: Int)`
  - `getAllFeatureAccessAttempts()`

- ✅ Updated `getEntitlementState()` to return `SUPPORTED` instead of `SUBSCRIBED`

**Exponential Backoff Algorithm**:
```kotlin
fun calculateNextThreshold(isSupporter: Boolean): Int {
    return if (isSupporter) {
        // Supporters (yearly reminders): 50, 100, 200 (less aggressive)
        when (donationPromptCount) {
            0 -> 50
            1 -> 100
            else -> 200
        }
    } else {
        // Non-supporters: 10, 25, 50, 100, 200, 400 (capped)
        when (donationPromptCount) {
            0 -> 10
            1 -> 25
            2 -> 50
            3 -> 100
            4 -> 200
            else -> 400 // Cap at 400
        }
    }
}
```

---

#### 1.3 UsageTrackingManager.kt ✅ DONE
**File**: `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/core/iap/UsageTrackingManager.kt`

**Changes Made**:
- ✅ Complete rewrite from feature-gating model to exponential backoff model
- ✅ Removed `recordFeatureAccessAttempt()` method
- ✅ Updated `recordHymnRead()` to accept `isSupporter: Boolean` parameter
- ✅ Added `shouldShowDonationPrompt(isSupporter: Boolean): Boolean`
- ✅ Added `recordPromptShown(isSupporter: Boolean)`
- ✅ Added `recordDonationMade()` - resets all counters
- ✅ Added `getNextPromptThreshold(): Int`
- ✅ Added `isYearlyReminder(isSupporter: Boolean): Boolean`
- ✅ Updated `UsageStats` data class to include `promptCount` and `lastDonationDate`

**Key Logic**:
```kotlin
// Supporters get 365-day grace period
if (isSupporter && !storage.shouldShowYearlyReminder()) {
    return false // No prompt within grace period
}

// Use exponential backoff based on prompt count
val hymnsRead = storage.hymnsReadCount
val nextThreshold = storage.nextPromptThreshold
return hymnsRead >= nextThreshold
```

---

#### 1.4 Platform-Specific PurchaseManager Updates ⏳ TODO

**Files to Update**:
- `/composeApp/src/androidMain/kotlin/com/kobby/hymnal/core/iap/PurchaseManager.android.kt`
- `/composeApp/src/iosMain/kotlin/com/kobby/hymnal/core/iap/IosPurchaseManager.ios.kt`

**Required Changes**:
1. Update any references from `EntitlementState.SUBSCRIBED` to `EntitlementState.SUPPORTED`
2. Ensure `recordPurchase()` calls `storage.recordDonation()`
3. Verify initialization calls `usageTracker.initialize()`
4. No changes needed to product IDs (still `support_basic` and `support_generous`)

**Search Pattern**:
```bash
grep -r "SUBSCRIBED" composeApp/src/androidMain --include="*.kt"
grep -r "SUBSCRIBED" composeApp/src/iosMain --include="*.kt"
```

---

### PHASE 2: Remove Feature Gates ✅ COMPLETED

#### 2.1 HymnDetailScreen.kt ✅ DONE
**File**: `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/hymns/HymnDetailScreen.kt`

**Changes Made**:
- ✅ Updated `LaunchedEffect` to use new exponential backoff:
  ```kotlin
  val isSupporter = entitlementInfo.hasSupported
  val shouldShowPrompt = purchaseManager.usageTracker.recordHymnRead(isSupporter)
  
  if (shouldShowPrompt) {
      purchaseManager.usageTracker.recordPromptShown(isSupporter)
      val isYearlyReminder = purchaseManager.usageTracker.isYearlyReminder(isSupporter)
      navigator.push(PayWallScreen(isYearlyReminder = isYearlyReminder))
  }
  ```

- ✅ Removed feature gate from `onFavoriteClick`:
  ```kotlin
  // All users can use favorites now - no gates!
  scope.launch {
      if (isFavorite) {
          repository.removeFromFavorites(hymnId)
      } else {
          repository.addToFavorites(hymnId)
      }
      isFavorite = !isFavorite
  }
  ```

- ✅ Removed feature gate from `onFontSettingsClick`:
  ```kotlin
  // All users can customize fonts now - no gates!
  showFontSettings = true
  ```

- ✅ Removed all `canAccessFeature()` checks
- ✅ Removed all `recordFeatureAccessAttempt()` calls

---

#### 2.2 FavoritesScreen.kt ✅ DONE
**File**: `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/more/FavoritesScreen.kt`

**Changes Made**:
- ✅ Removed import: `import com.kobby.hymnal.core.iap.PremiumFeature`
- ✅ Removed import: `import com.kobby.hymnal.core.iap.PremiumFeatureAccess`
- ✅ Removed `PremiumFeatureAccess` wrapper
- ✅ Direct rendering of `FavoritesContent` without any gates
- ✅ Added comment: `// All users can access favorites now - no gates!`

**Before**: 69 lines with gating wrapper  
**After**: 62 lines, direct access

---

#### 2.3 HighlightsScreen.kt ✅ DONE
**File**: `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/more/HighlightsScreen.kt`

**Changes Made**:
- ✅ Removed import: `import com.kobby.hymnal.core.iap.PremiumFeature`
- ✅ Removed import: `import com.kobby.hymnal.core.iap.PremiumFeatureAccess`
- ✅ Removed `PremiumFeatureAccess` wrapper
- ✅ Direct rendering of `HighlightsContent` without any gates
- ✅ Added comment: `// All users can access highlights now - no gates!`

**Before**: 69 lines with gating wrapper  
**After**: 62 lines, direct access

---

### PHASE 3: Update UI & Messaging 🚧 IN PROGRESS

#### 3.1 PayWallScreen.kt ✅ PARTIALLY DONE
**File**: `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/settings/PayWallScreen.kt`

**Changes Made**:
- ✅ Added `isYearlyReminder: Boolean = false` parameter to class constructor
- ✅ Passed `isYearlyReminder` to `PayWallContent`
- ✅ Added `recordDonationMade()` call on successful purchase:
  ```kotlin
  if (success) {
      // Record donation to reset prompt counters
      purchaseManager.usageTracker.recordDonationMade()
      // ...navigate back...
  }
  ```

**Still TODO**:
- [ ] Consider updating successful restore to also call `recordDonationMade()`

---

#### 3.2 PayWall.kt 🚧 IN PROGRESS
**File**: `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/settings/PayWall.kt`

**Changes Made**:
- ✅ Added `isYearlyReminder: Boolean = false` parameter to `PayWallContent`

**Still TODO**:
- [ ] Update `PaywallHeader()` to accept `isYearlyReminder` parameter
- [ ] Implement conditional messaging in `PaywallHeader`:
  ```kotlin
  @Composable
  private fun PaywallHeader(isYearlyReminder: Boolean = false) {
      val title = if (isYearlyReminder) {
          stringResource(Res.string.donation_reminder_title)
      } else {
          stringResource(Res.string.donation_prompt_title)
      }
      // ... similar for subtitle ...
  }
  ```
- [ ] Pass `isYearlyReminder` to `PaywallHeader()` call (around line 138)
- [ ] Update `FeaturesCard()` to show "What Your Support Enables" instead of "Premium Features"
- [ ] Update `SharedMinistryCard()` messaging
- [ ] Update button text based on `isYearlyReminder`:
  - First-time: "Support Development"
  - Yearly reminder: "Support Again"

**Current String Resources Mapping**:
| Old Key | New Key | New Value |
|---------|---------|-----------|
| `settings_title_paywall` | `donation_prompt_title` | "Thank you for using our app!" |
| - | `donation_reminder_title` | "Still enjoying the hymnal?" |
| `settings_subtitle_paywall` | `donation_prompt_subtitle` | "All features are free forever. If you find this app helpful, consider supporting development." |
| - | `donation_reminder_subtitle` | "Thanks for your past support! If you're still finding value, another small contribution helps us continue." |
| `settings_section_features_title` | `donation_benefits_title` | "What Your Support Enables" |
| `settings_section_features_subtitle` | `donation_benefits_subtitle` | "Help us keep this app free for everyone" |

---

#### 3.3 String Resources Updates ⏳ TODO

**Files to Update**:
- `/composeApp/src/commonMain/composeResources/values/strings.xml` (Android)
- Consider iOS localization if using separate files

**New Strings to Add**:
```xml
<!-- Donation Prompt (First-time users) -->
<string name="donation_prompt_title">Thank you for using our app!</string>
<string name="donation_prompt_subtitle">All features are free forever. If you find this app helpful, consider supporting development.</string>

<!-- Donation Reminder (Yearly for supporters) -->
<string name="donation_reminder_title">Still enjoying the hymnal?</string>
<string name="donation_reminder_subtitle">Thanks for your past support! If you're still finding value, another small contribution helps us continue.</string>

<!-- Benefits Section -->
<string name="donation_benefits_title">What Your Support Enables</string>
<string name="donation_benefits_subtitle">Help us keep this app free for everyone</string>

<!-- Benefits List (replacing feature list) -->
<string name="donation_benefit_maintenance">Keep the app free for everyone</string>
<string name="donation_benefit_features">Add more hymns and features</string>
<string name="donation_benefit_improvement">Maintain and improve the app</string>

<!-- Buttons -->
<string name="donation_button_support">Support Development</string>
<string name="donation_button_support_again">Support Again</string>
<string name="donation_button_maybe_later">Maybe Later</string>
<string name="donation_button_no_thanks">No Thanks</string>
```

**Strings to Update**:
```xml
<!-- Keep but update meaning -->
<string name="settings_option_basic_title">GH₵ 10 / One-time</string>
<string name="settings_option_basic_subtitle">Support at student-friendly rate</string>
<string name="settings_option_generous_title">GH₵ 20 / One-time</string>
<string name="settings_option_generous_subtitle">Support what you can</string>
```

---

### PHASE 4: Delete Obsolete Files ⏳ PENDING

#### 4.1 Files to Delete

**File 1**: `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/core/iap/PremiumFeatureGate.kt`
- **Reason**: No longer needed - all features are free
- **Used by**: Nothing (after Phase 2 completion)
- **Action**: Delete entire file

**File 2**: `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/core/iap/SupportSheetTrigger.kt`
- **Reason**: No longer needed - no feature-based triggering
- **Contains**: `PremiumFeatureAccess` composable and `canAccessPremiumFeature()` function
- **Action**: Delete entire file

**Command to Execute**:
```bash
rm /Users/kobby/AndroidStudioProjects/Hymnal-CMP/composeApp/src/commonMain/kotlin/com/kobby/hymnal/core/iap/PremiumFeatureGate.kt
rm /Users/kobby/AndroidStudioProjects/Hymnal-CMP/composeApp/src/commonMain/kotlin/com/kobby/hymnal/core/iap/SupportSheetTrigger.kt
```

---

### PHASE 5: Testing & Validation ⏳ PENDING

#### 5.1 Build and Compilation

**Step 1: Clean Build**
```bash
cd /Users/kobby/AndroidStudioProjects/Hymnal-CMP
./gradlew clean
```

**Step 2: Check for Compilation Errors**
```bash
./gradlew compileKotlin
```

**Expected Errors to Fix**:
- Any remaining references to `PremiumFeature` enum
- Any remaining calls to `canAccessFeature()`
- Any imports of deleted files (`PremiumFeatureGate`, `SupportSheetTrigger`)

**Search for Remaining References**:
```bash
grep -r "PremiumFeature" composeApp/src --include="*.kt"
grep -r "canAccessFeature" composeApp/src --include="*.kt"
grep -r "PremiumFeatureGate" composeApp/src --include="*.kt"
grep -r "PremiumFeatureAccess" composeApp/src --include="*.kt"
```

---

#### 5.2 Manual Testing Checklist

**A. Free Feature Access (All Users)**
- [ ] Install app fresh on device/emulator
- [ ] Verify all features work immediately:
  - [ ] Tap favorites button - adds to favorites without prompt
  - [ ] Tap font settings - opens font customization without prompt
  - [ ] Navigate to Favorites screen - shows content without prompt
  - [ ] Navigate to Highlights screen - shows content without prompt
  - [ ] Create highlights - works without prompt
- [ ] Verify no "Premium" badges or locks anywhere in UI

**B. Exponential Backoff (Non-Supporters)**
- [ ] Read 10 hymns - donation prompt appears
- [ ] Dismiss prompt - can continue using app
- [ ] Read 25 more hymns (35 total) - second prompt appears
- [ ] Dismiss again - can continue
- [ ] Read 50 more hymns (85 total) - third prompt appears
- [ ] Verify intervals: 10, 25, 50, 100, 200, 400 hymns
- [ ] Verify prompt is always dismissible (X button works)

**C. Supporter Experience**
- [ ] Make donation (basic tier GH₵ 10)
- [ ] Verify prompt disappears immediately
- [ ] Verify counter resets to 0
- [ ] Read hymns - no prompts appear
- [ ] Manipulate timestamp to simulate 365 days passing
- [ ] Verify gentle yearly reminder appears
- [ ] Verify yearly reminder has different messaging
- [ ] Verify yearly reminder intervals: 50, 100, 200 hymns

**D. Edge Cases**
- [ ] Close app mid-session - verify counters persist
- [ ] Reinstall app - verify fresh start
- [ ] Restore purchases - verify treated as new donation
- [ ] App update - verify existing supporters keep status
- [ ] Network offline - verify offline behavior

**E. UI/UX Validation**
- [ ] Donation prompt looks good on small screens
- [ ] Donation prompt scrolls properly if needed
- [ ] Close button is clearly visible
- [ ] Messaging feels natural, not pushy
- [ ] No typos or formatting issues

---

#### 5.3 Testing Tools

**Simulate Time Passage** (for yearly reminder testing):
```kotlin
// In PurchaseStorage.kt or test environment
// Temporarily override shouldShowYearlyReminder() for testing:
fun shouldShowYearlyReminderForTesting(): Boolean {
    val lastDonation = lastDonationDate ?: return false
    // Use 1 minute instead of 365 days for testing
    val testInterval = 60L * 1000 // 1 minute in milliseconds
    val currentTime = Clock.System.now().toEpochMilliseconds()
    return (currentTime - lastDonation) >= testInterval
}
```

**Reset All Data** (for fresh testing):
```kotlin
// Create a test menu option to reset:
fun resetAllDataForTesting() {
    storage.clearAll()
    storage.donationPromptCount = 0
    storage.hymnsReadCount = 0
    storage.nextPromptThreshold = 10
    storage.lastDonationDate = null
}
```

---

### PHASE 6: Documentation Updates ⏳ PENDING

#### 6.1 Update Implementation Review

**File**: `/docs/IMPLEMENTATION_REVIEW_JAN_2026.md`

**Add new section at the end**:
```markdown
---

## Model Evolution - January 8, 2026

### Transition to Fully Free Model

**Reason for Change**: User feedback indicated that feature gates created friction and reduced engagement. Users wanted to try all features before deciding to support. The generous freemium model, while better than hard paywalls, still created barriers to full app experience.

**New Approach**:
- ✅ **All features completely free** - No gates on favorites, highlights, or font customization
- ✅ **Exponential backoff donation prompts** - Less frequent over time (10, 25, 50, 100, 200, 400 hymns)
- ✅ **Supporter relief** - 365 days without prompts after donation
- ✅ **Gentle yearly reminders** - Soft asks for existing supporters (50, 100, 200 hymn intervals)
- ✅ **Always dismissible** - Users can always close prompts and continue

**Implementation Details**:
- Removed `PremiumFeature` enum entirely
- Implemented exponential backoff in `UsageTrackingManager`
- Updated all screens to remove feature gates
- Modified messaging to focus on gratitude and optional support

**Expected Impact**:
- Increased user satisfaction (no feature friction)
- Higher feature adoption rates (immediate access)
- More engaged user base (full app experience)
- Sustainable support through goodwill (not forced gates)
- Better conversion rates (users support after seeing value)

**Metrics to Monitor**:
- Feature usage rates (expect 40%+ favorites, 25%+ highlights)
- Donation conversion at each interval
- User retention (7-day and 30-day)
- App store reviews (expect improved ratings)
- Support feedback (listen for complaints about frequency)

---

**Last Updated**: January 8, 2026  
**Current Model**: Free with Optional Donation Prompts  
**Status**: Implementation in Progress
```

---

#### 6.2 Archive Old Documentation

**Create Archive Folder**:
```bash
mkdir -p /Users/kobby/AndroidStudioProjects/Hymnal-CMP/docs/_archived_feature_gating_model
```

**Move Old Docs**:
```bash
mv /Users/kobby/AndroidStudioProjects/Hymnal-CMP/docs/GENEROUS_FREEMIUM_IMPLEMENTATION.md \
   /Users/kobby/AndroidStudioProjects/Hymnal-CMP/docs/_archived_feature_gating_model/

mv /Users/kobby/AndroidStudioProjects/Hymnal-CMP/docs/FEATURE_GATING_USAGE_GUIDE.md \
   /Users/kobby/AndroidStudioProjects/Hymnal-CMP/docs/_archived_feature_gating_model/

mv /Users/kobby/AndroidStudioProjects/Hymnal-CMP/docs/FREEMIUM_TESTING_GUIDE.md \
   /Users/kobby/AndroidStudioProjects/Hymnal-CMP/docs/_archived_feature_gating_model/
```

**Add README to Archive**:
Create `/docs/_archived_feature_gating_model/README.md`:
```markdown
# Archived: Feature Gating Model Documentation

This folder contains documentation for the previous "Generous Freemium" model that used feature gates.

**Archived Date**: January 8, 2026  
**Reason**: Transitioned to fully free model with donation prompts

## Previous Model Summary

- **Philosophy**: "Worship is Free, Tools are Premium"
- **Free Features**: Hymn reading, search, history
- **Gated Features**: Favorites, highlights, font customization
- **Prompt Trigger**: After 10th hymn (fixed)
- **Result**: Good but created friction for users

## New Model

See `/docs/FREE_DONATION_MODEL_IMPLEMENTATION_PLAN.md` for current implementation.

**Philosophy**: "Everything is Free, Support is Appreciated"
```

---

#### 6.3 Update Main README

**File**: `/README.md`

**Find and update monetization section**:
```markdown
## Monetization Model

**All Features Free**: Every feature in the Anglican Hymnal app is completely free to use—no gates, no locks, no restrictions. All users can:
- Mark unlimited favorites
- Create highlights in hymns
- Customize font size and style
- Access full hymn catalog
- Use all search and browsing features

**Optional Support**: Users are occasionally invited to support development through gentle donation prompts. These appear less frequently over time using exponential backoff:
- First prompt: After 10 hymns
- Subsequent prompts: 25, 50, 100, 200, 400 hymns apart
- Always dismissible - users can continue using the app freely

**Supporter Benefits**: 
- Those who support receive relief from donation prompts for approximately one year
- After 365 days, a very gentle yearly reminder appears (much less frequent)
- Supporters help keep the app free for everyone

**Pricing** (Ghana Market):
- **GH₵ 10** - Student-friendly support rate
- **GH₵ 20** - Generous supporter rate
- Both unlock the same benefits: 365 days of prompt-free experience
- Payment via MTN MoMo or Telecel Cash

**Philosophy**: We believe worship should be completely free. Your support helps us maintain and improve this app for the entire community.
```

---

## Implementation Checklist Summary

Use this as a quick reference for remaining work:

### ✅ COMPLETED
- [x] Core architecture changes (EntitlementState, PurchaseStorage, UsageTrackingManager)
- [x] Removed feature gates from all screens
- [x] Updated HymnDetailScreen with exponential backoff logic
- [x] Added isYearlyReminder parameter to PayWall components

### 🚧 IN PROGRESS
- [ ] Update PaywallHeader messaging (PayWall.kt line ~241)
- [ ] Pass isYearlyReminder to PaywallHeader (PayWall.kt line ~138)
- [ ] Update FeaturesCard component
- [ ] Update SharedMinistryCard messaging
- [ ] Update button text based on isYearlyReminder

### ⏳ TODO
- [ ] Update platform-specific PurchaseManager files (Android/iOS)
- [ ] Add new string resources
- [ ] Delete obsolete files (PremiumFeatureGate.kt, SupportSheetTrigger.kt)
- [ ] Build and fix compilation errors
- [ ] Complete manual testing
- [ ] Update documentation
- [ ] Archive old docs

---

## Key Files Reference

### Core IAP Files
- `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/core/iap/EntitlementState.kt`
- `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/core/iap/PurchaseStorage.kt`
- `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/core/iap/UsageTrackingManager.kt`
- `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/core/iap/PurchaseManager.kt`

### Screen Files
- `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/hymns/HymnDetailScreen.kt`
- `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/more/FavoritesScreen.kt`
- `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/more/HighlightsScreen.kt`
- `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/settings/PayWallScreen.kt`
- `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/settings/PayWall.kt`

### Platform-Specific Files
- `/composeApp/src/androidMain/kotlin/com/kobby/hymnal/core/iap/PurchaseManager.android.kt`
- `/composeApp/src/iosMain/kotlin/com/kobby/hymnal/core/iap/IosPurchaseManager.ios.kt`

### Documentation
- `/docs/IMPLEMENTATION_REVIEW_JAN_2026.md`
- `/docs/FREE_DONATION_MODEL_IMPLEMENTATION_PLAN.md` (this file)
- `/README.md`

---

## Exponential Backoff Visual Guide

### Non-Supporter Journey
```
Hymns Read:    10      35      85      185     385     785     1185
                |       |       |       |       |       |       |
Prompt #:       1       2       3       4       5       6       7
Interval:      10  ←   25  ←   50  ←  100  ←  200  ←  400  ←  400
```

### Supporter Journey (After 365 Days)
```
Hymns Since:   50      150     350     550
                |       |       |       |
Prompt #:       1       2       3       4
Interval:      50  ←  100  ←  200  ←  200
```

### Grace Period (First 365 Days for Supporters)
```
Days:    0                                          365
         |-------------------------------------------|
         Donation Made                    First Gentle Reminder
         
         During this period: NO PROMPTS AT ALL
```

---

## Troubleshooting Guide

### Build Errors

**Error**: `Unresolved reference: PremiumFeature`
- **Cause**: Old code still referencing deleted enum
- **Fix**: Search for `PremiumFeature` in codebase and remove/update references
- **Command**: `grep -r "PremiumFeature" composeApp/src --include="*.kt"`

**Error**: `Unresolved reference: canAccessFeature`
- **Cause**: Old code still checking feature access
- **Fix**: Remove the check - all features are now free
- **Pattern**: Replace `if (entitlementInfo.canAccessFeature(...))` with direct action

**Error**: `Function 'recordFeatureAccessAttempt' not found`
- **Cause**: This method was removed from UsageTrackingManager
- **Fix**: Remove the call - we no longer track feature access attempts

### Runtime Issues

**Issue**: Donation prompt never appears
- **Check**: `storage.nextPromptThreshold` is set (default: 10)
- **Check**: `recordHymnRead()` is being called in HymnDetailScreen
- **Debug**: Add logging to `shouldShowDonationPrompt()`

**Issue**: Donation prompt appears too frequently
- **Check**: `recordPromptShown()` is being called after showing prompt
- **Check**: `nextPromptThreshold` is being incremented correctly
- **Debug**: Log prompt count and next threshold values

**Issue**: Supporters still seeing prompts immediately
- **Check**: `recordDonationMade()` is called on purchase success
- **Check**: `lastDonationDate` is set correctly
- **Check**: `shouldShowYearlyReminder()` returns false within 365 days

---

## Success Criteria

### Technical Success
- ✅ All features accessible without payment
- ✅ Zero compilation errors
- ✅ All tests passing
- ✅ No crashes related to IAP changes
- ✅ Exponential backoff working correctly
- ✅ Yearly reminders working for supporters

### User Experience Success
- ✅ Positive app store reviews mentioning "free" and "generous"
- ✅ Feature usage rates: 40%+ favorites, 25%+ highlights
- ✅ Low complaint rate about prompt frequency (<5%)
- ✅ High dismissal engagement (>50% interact with prompt)
- ✅ Improved retention rates

### Business Success
- ✅ Donation conversion: 15-20% overall
- ✅ First prompt (10 hymns): 8-12% conversion
- ✅ Yearly reminder: 10-20% re-support rate
- ✅ Tier distribution: 60% basic, 40% generous
- ✅ Sustainable support revenue maintained or improved

---

## Next Steps for LLM Continuation

If you're an LLM picking up this work:

1. **Start here**: Read this entire document first
2. **Check status**: Review the "Implementation Status Tracker" section
3. **Priority tasks**: 
   - Complete PayWall.kt messaging updates (Phase 3.2)
   - Update string resources (Phase 3.3)
   - Test compilation (Phase 5.1)
4. **Don't skip**: Testing is critical - follow Phase 5 carefully
5. **Before claiming done**: All checklist items must be ✅

**Key Context**:
- We're moving from feature gates to pure donation prompts
- All features are now free
- Exponential backoff reduces prompt frequency over time
- Supporters get 365 days of peace, then gentle reminders

**Files needing immediate attention**:
1. `/composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/settings/PayWall.kt` (line ~241 and ~138)
2. String resources file (add new donation messaging)
3. Platform-specific PurchaseManager files (update SUBSCRIBED → SUPPORTED)

Good luck! 🚀

---

**Document Version**: 1.0  
**Last Updated**: January 8, 2026  
**Maintained By**: Development Team

