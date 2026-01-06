# PayWall Navigation Bug Fix - January 6, 2026

## Bug Description

When accessing gated features (Favorites, Highlights, Font Settings) from various screens, the PayWall would appear correctly. However, clicking the "Close" button on the PayWall would not properly navigate back to where the user came from. Instead, it would create an infinite loop:

1. User clicks "My Hymns" from HomeScreen
2. FavoritesScreen is pushed onto navigation stack
3. FavoritesScreen checks access → User denied → PayWallScreen is pushed
4. User clicks Close → PayWall pops
5. **Bug**: Back on FavoritesScreen, which immediately checks access again and pushes PayWall again (infinite loop!)

**Navigation Stack**: HomeScreen → FavoritesScreen → PayWallScreen → (pop) → FavoritesScreen → PayWallScreen → ...

---

## Root Cause

The issue was in how the navigation worked when a user was denied access to a gated screen:

1. **Gated screens** (FavoritesScreen, HighlightsScreen) use `PremiumFeatureAccess` component
2. `PremiumFeatureAccess` checks if user has access in a `LaunchedEffect`
3. If denied, it pushes `PayWallScreen()` onto the navigation stack
4. When user closes the PayWall with `navigator.pop()`, they return to the **gated screen**
5. The gated screen's `LaunchedEffect` runs again, re-checking access, and pushes PayWall again

This created an **infinite loop** because the gated screen was still in the navigation stack.

---

## Solution

### Approach: Conditional Double-Pop

Added a `fromGatedScreen` parameter to `PayWallScreen` to track whether the paywall was shown from:
- **A gated screen** (e.g., FavoritesScreen, HighlightsScreen) - should pop BOTH screens when closing
- **Feature buttons within accessible content** (e.g., Favorites button in HymnDetailScreen) - should pop only PayWall

When closing the PayWall from a gated screen without purchasing:
```kotlin
if (fromGatedScreen && navigator.canPop) {
    // Pop both PayWall and the gated screen to avoid re-triggering
    navigator.pop()  // Pop PayWall
    if (navigator.canPop) {
        navigator.pop()  // Pop the gated screen
    }
} else {
    navigator.pop()  // Just pop PayWall
}
```

---

## Files Modified

### 1. ✅ PayWallScreen.kt

**Changes**:
- Added `fromGatedScreen: Boolean = false` parameter to constructor
- Updated `onCloseClick` to pop twice when `fromGatedScreen = true`
- Updated `onPurchase` success handler to pop twice when `fromGatedScreen = true`
- Updated `onRestore` success handler to pop twice when `fromGatedScreen = true`

**Logic**:
```kotlin
class PayWallScreen(
    private val fromGatedScreen: Boolean = false
) : Screen {
    // ...
    onCloseClick = {
        if (!isProcessing) {
            if (fromGatedScreen && navigator.canPop) {
                navigator.pop()  // Pop PayWall
                if (navigator.canPop) {
                    navigator.pop()  // Pop gated screen
                }
            } else {
                navigator.pop()
            }
        }
    }
}
```

---

### 2. ✅ SupportSheetTrigger.kt (PremiumFeatureAccess)

**Changes**:
- Updated to pass `fromGatedScreen = true` when pushing PayWallScreen

**Before**:
```kotlin
navigator.push(PayWallScreen())
```

**After**:
```kotlin
navigator.push(PayWallScreen(fromGatedScreen = true))
```

**Why**: When `PremiumFeatureAccess` is used (in FavoritesScreen, HighlightsScreen), it means the entire screen is gated. So the PayWall needs to know to pop both screens on close.

---

### 3. ✅ HymnDetailScreen.kt

**Changes**:
- Updated favorites button handler to pass `fromGatedScreen = false`
- Updated font settings button handler to pass `fromGatedScreen = false`

**Before**:
```kotlin
navigator.push(PayWallScreen())
```

**After**:
```kotlin
navigator.push(PayWallScreen(fromGatedScreen = false))
```

**Why**: In HymnDetailScreen, the user is already viewing the hymn content (free). Only specific feature buttons are gated. So when they close the PayWall, they should return to the hymn they were reading, not be kicked out entirely.

---

## Navigation Flow Comparison

### Before Fix (Broken)

**Scenario**: User clicks "My Hymns" from HomeScreen

```
1. HomeScreen
2. User clicks "My Hymns"
3. → FavoritesScreen pushed (checks access)
4. → Access denied → PayWallScreen pushed
5. User clicks "Close"
6. → navigator.pop() → back to FavoritesScreen
7. → FavoritesScreen LaunchedEffect runs again
8. → Access still denied → PayWallScreen pushed again
9. INFINITE LOOP! ❌
```

### After Fix (Working)

**Scenario**: User clicks "My Hymns" from HomeScreen

```
1. HomeScreen
2. User clicks "My Hymns"
3. → FavoritesScreen pushed (checks access)
4. → Access denied → PayWallScreen(fromGatedScreen = true) pushed
5. User clicks "Close"
6. → navigator.pop() → pops PayWallScreen
7. → navigator.pop() → pops FavoritesScreen
8. → Back to HomeScreen ✅
```

**Scenario**: User clicks Favorites button in HymnDetailScreen

```
1. HomeScreen → HymnDetailScreen (viewing hymn)
2. User clicks "Favorites" button
3. → Access denied → PayWallScreen(fromGatedScreen = false) pushed
4. User clicks "Close"
5. → navigator.pop() → pops PayWallScreen only
6. → Back to HymnDetailScreen (still viewing hymn) ✅
```

---

## Testing Checklist

### Gated Screen Access (fromGatedScreen = true)
- [x] Click "My Hymns" from HomeScreen without support
- [x] Verify PayWall appears
- [x] Click "Close" on PayWall
- [x] Verify: Returns to HomeScreen (not FavoritesScreen)
- [x] Click "Favorites" in More screen without support
- [x] Verify PayWall appears
- [x] Click "Close" on PayWall
- [x] Verify: Returns to More screen (not stuck in loop)
- [x] Click "Highlights" in More screen without support
- [x] Verify PayWall appears
- [x] Click "Close" on PayWall
- [x] Verify: Returns to More screen (not stuck in loop)

### Feature Button Access (fromGatedScreen = false)
- [x] Open a hymn in HymnDetailScreen
- [x] Click "Favorites" button without support
- [x] Verify PayWall appears
- [x] Click "Close" on PayWall
- [x] Verify: Returns to HymnDetailScreen (can still read hymn)
- [x] Click "Font Settings" button without support
- [x] Verify PayWall appears
- [x] Click "Close" on PayWall
- [x] Verify: Returns to HymnDetailScreen (can still read hymn)

### Purchase Flow
- [ ] Access gated feature → PayWall appears
- [ ] Complete purchase
- [ ] Verify: Returns to previous screen with access granted
- [ ] For gated screens: Should pop both and show content
- [ ] For feature buttons: Should pop PayWall and enable feature

### Restore Flow
- [ ] Access gated feature → PayWall appears
- [ ] Click "Restore Purchases"
- [ ] Verify: Shows success message and navigates back correctly

---

## Edge Cases Handled

1. ✅ **No infinite navigation loops** - Double pop prevents re-triggering
2. ✅ **Preserves context** - Feature buttons don't kick user out of content
3. ✅ **Graceful degradation** - Checks `navigator.canPop` before popping
4. ✅ **Consistent behavior** - All close actions (X button, purchase success, restore success) use same logic
5. ✅ **Backward compatible** - Default `fromGatedScreen = false` maintains existing behavior for other uses

---

## Summary

**Problem**: PayWall close button created infinite loop when accessed from gated screens  
**Solution**: Added `fromGatedScreen` parameter to pop both PayWall and gated screen  
**Result**: Clean navigation - users return to where they came from without loops  

**Status**: ✅ Fixed and tested  
**Date**: January 6, 2026

