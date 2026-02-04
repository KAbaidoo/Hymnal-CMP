# Testing Checklist - Free Donation Model

**Date**: January 8, 2026  
**Model**: Everything is Free, Support is Appreciated  
**Status**: Ready for Testing

---

## Pre-Testing Setup

- [ ] Clean build: `./gradlew clean`
- [ ] Fresh install on test device
- [ ] Clear app data (Settings → Apps → Hymnal → Clear Data)
- [ ] Verify internet connection for donation testing

---

## ✅ Phase 1: Free Feature Access

### Test: All Features Work Without Payment

- [ ] **Install fresh** - New user experience
- [ ] **Open app** - No paywall on launch
- [ ] **Read hymn** - Content displays normally
- [ ] **Tap Favorites button** - Works immediately, no prompt
- [ ] **Add to favorites** - Successfully saved
- [ ] **Navigate to Favorites screen** - Opens without prompt, shows favorited hymn
- [ ] **Remove from favorites** - Works correctly
- [ ] **Tap Font Settings** - Opens immediately, no prompt
- [ ] **Change font size** - Successfully applied
- [ ] **Change font family** - Successfully applied
- [ ] **Create highlight** - Works immediately
- [ ] **Navigate to Highlights screen** - Opens without prompt
- [ ] **View highlighted hymn** - Displays correctly

**Expected Result**: ✅ All features accessible without any payment or prompts

---

## ✅ Phase 2: Exponential Backoff (Non-Supporters)

### Test: First Prompt at 10 Hymns

- [ ] **Read hymn 1-9** - No donation prompt appears
- [ ] **Read hymn 10** - Donation prompt appears
- [ ] **Verify prompt title** - "Thank you for using our app!"
- [ ] **Verify prompt subtitle** - "All features are free forever..."
- [ ] **Verify button text** - "Support Development"
- [ ] **Verify features list** - Shows donation benefits, not premium features
- [ ] **Tap X button** - Prompt closes, can continue using app
- [ ] **Continue reading hymns** - App works normally

**Expected Result**: ✅ Prompt appears at exactly 10 hymns, is dismissible

### Test: Second Prompt at 25 More Hymns (35 Total)

- [ ] **Read hymns 11-34** - No prompt appears
- [ ] **Read hymn 35** - Second donation prompt appears
- [ ] **Dismiss prompt** - Can continue using app

**Expected Result**: ✅ Second prompt at hymn 35 (25 hymns after first)

### Test: Third Prompt at 50 More Hymns (85 Total)

- [ ] **Read hymns 36-84** - No prompt appears
- [ ] **Read hymn 85** - Third donation prompt appears
- [ ] **Dismiss prompt** - Can continue using app

**Expected Result**: ✅ Third prompt at hymn 85 (50 hymns after second)

### Test: Subsequent Intervals

- [ ] **Read to hymn 185** - Fourth prompt appears (100 hymns after third)
- [ ] **Read to hymn 385** - Fifth prompt appears (200 hymns after fourth)
- [ ] **Read to hymn 785** - Sixth prompt appears (400 hymns after fifth - capped)
- [ ] **Read to hymn 1185** - Seventh prompt appears (400 hymns after sixth - still capped)

**Expected Result**: ✅ Intervals follow exponential backoff: 10, 25, 50, 100, 200, 400 (capped)

---

## ✅ Phase 3: Donation Flow

### Test: Basic Tier Donation (GH₵ 10)

- [ ] **Trigger donation prompt** - Read hymns or tap "Support Development"
- [ ] **Select Basic tier** - GH₵ 10 option selected
- [ ] **Verify price display** - Shows "GH₵ 10 / One-time"
- [ ] **Verify subtitle** - "Support at student-friendly rate"
- [ ] **Tap "Support Development"** - Payment flow initiates
- [ ] **Complete payment** - Use test payment method
- [ ] **Verify success** - Prompt closes, returns to app
- [ ] **Read more hymns** - No prompts appear

**Expected Result**: ✅ Donation successful, prompts stop appearing

### Test: Generous Tier Donation (GH₵ 20)

- [ ] **Fresh install** - Start over with new user
- [ ] **Trigger donation prompt**
- [ ] **Select Generous tier** - GH₵ 20 option selected
- [ ] **Verify "Generous" badge** - Displays on card
- [ ] **Complete donation**
- [ ] **Verify success** - Prompts stop

**Expected Result**: ✅ Generous tier works correctly

### Test: Restore Purchases

- [ ] **Uninstall app**
- [ ] **Reinstall app**
- [ ] **Tap "Support Development"**
- [ ] **Tap "Restore Purchases"**
- [ ] **Verify restoration** - Previous donation recognized
- [ ] **Read hymns** - No prompts appear

**Expected Result**: ✅ Restore purchases works

---

## ✅ Phase 4: Supporter Experience

### Test: Supporter Experience

- [ ] **Make donation** - Complete purchase
- [ ] **Verify prompt counter reset** - Hymns read = 0
- [ ] **Read hymns** - No prompts appear

**Notes for testing**:
- Restoring purchases should be treated as a donation (no more prompts)
- Simulate restores and verify supporters are not shown prompts

**Expected Result**: ✅ Supporters will not be prompted again after donation

---

## ✅ Phase 5: Edge Cases

### Test: App Lifecycle

- [ ] **Close app mid-session** - Force close
- [ ] **Reopen app** - Hymn counter persists
- [ ] **Continue reading** - Prompt appears at correct threshold

**Expected Result**: ✅ Counters persist across app sessions

### Test: Offline Behavior

- [ ] **Disable internet**
- [ ] **Use all features** - Favorites, highlights, fonts work
- [ ] **Read hymns** - Counter increments
- [ ] **Trigger prompt** - Shows but payment disabled
- [ ] **Enable internet** - Can complete donation

**Expected Result**: ✅ App works offline, donation requires internet

### Test: Prompt Dismissal

- [ ] **Trigger prompt**
- [ ] **Tap X button** - Closes cleanly
- [ ] **Tap outside prompt** - Should not close (modal)
- [ ] **Back button (Android)** - Closes prompt
- [ ] **Swipe down (iOS)** - Closes prompt

**Expected Result**: ✅ Prompt is dismissible but intentional

---

## ✅ Phase 6: UI/UX Validation

### Test: Visual Quality

- [ ] **Small screen** (iPhone SE, small Android) - Prompt looks good
- [ ] **Large screen** (iPad, large Android) - Prompt looks good
- [ ] **Scrolling** - Prompt scrolls if content overflows
- [ ] **Dark mode** - Prompt readable in both modes
- [ ] **Accessibility** - Text is readable, buttons are tappable

**Expected Result**: ✅ Professional appearance on all devices

### Test: Messaging Quality

- [ ] **First prompt** - Tone is grateful, not pushy
- [ ] **No typos** - All text is correct
- [ ] **Cultural fit** - Messaging feels appropriate for Ghana
- [ ] **Payment methods** - MTN MoMo/Telecel Cash mentioned

**Expected Result**: ✅ Messaging is on-brand and culturally appropriate

---

## ✅ Phase 7: Performance

### Test: App Performance

- [ ] **Launch time** - App starts quickly
- [ ] **Hymn loading** - Fast, no delays
- [ ] **Prompt appearance** - Smooth transition
- [ ] **Memory usage** - No excessive memory consumption
- [ ] **Battery drain** - No unusual battery usage

**Expected Result**: ✅ Performance unchanged or improved

---

## 🐛 Bug Reporting Template

If any test fails, document:

```
**Test**: [Test name]
**Expected**: [What should happen]
**Actual**: [What actually happened]
**Steps to Reproduce**:
1. [Step 1]
2. [Step 2]
3. [etc.]
**Device**: [iOS/Android version]
**Build**: [Build number]
**Screenshots**: [If applicable]
```

---

## 📊 Success Criteria

### Must Pass (Blocking Issues)
- ✅ All features accessible without payment
- ✅ No crashes or fatal errors
- ✅ Prompts appear at correct intervals
- ✅ Donation flow works end-to-end
- ✅ Counters persist correctly

### Should Pass (Important Issues)
- ✅ UI looks professional on all devices
- ✅ Messaging tone is appropriate
- ✅ Restore purchases works
- ✅ Performance is acceptable

### Nice to Have (Minor Issues)
- ✅ Perfect alignment on all screen sizes
- ✅ Animations are smooth
- ✅ Dark mode perfect

---

## 🚀 Ready for Production Checklist

Before deploying to production:

- [ ] All Phase 1-7 tests passed
- [ ] No blocking bugs found
- [ ] Performance acceptable
- [ ] UI reviewed by design team
- [ ] Messaging reviewed by stakeholders
- [ ] Analytics tracking verified
- [ ] App store listing updated
- [ ] Support documentation updated
- [ ] Team trained on new model

---

**Testing Started**: _____________  
**Testing Completed**: _____________  
**Tested By**: _____________  
**Result**: ⬜ PASS / ⬜ FAIL  
**Notes**: _____________
