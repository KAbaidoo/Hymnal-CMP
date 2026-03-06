# Archived Documentation - Old Subscription Model

**Archive Date**: January 6, 2026  
**Reason**: Transition from trial-based subscription to generous freemium model

---

## ⚠️ IMPORTANT NOTICE

**These documents are OBSOLETE and should NOT be used for current development.**

They describe the **old subscription model** which has been completely removed from the codebase.

---

## What Was the Old Model?

### Trial-Based Subscription (REMOVED)
- 7-day free trial for all features
- Hard paywall after trial expiration
- Non-dismissible paywall (users forced to purchase)
- Product IDs: `yearly_subscription`, `onetime_purchase`

### Why It Was Changed

**Problem**: Not suitable for Ghanaian market
- Hard paywall felt like "commercializing worship"
- Trial pressure created negative user experience
- All-or-nothing approach didn't match cultural giving patterns

**Solution**: Generous Freemium Model
- Core worship features free forever (hymn reading, search)
- Premium convenience features (favorites, highlights, fonts) require support
- Soft gates - always dismissible
- Two pricing tiers: GH₵ 15 (accessible) and GH₵ 20 (generous)
- Cultural messaging: "Support this Ministry" not "Unlock Full App"

---

## Files in This Archive

These documents reference the **removed** trial and hard paywall system:

### Paywall Documentation
- `PAYWALL_GATE_IMPLEMENTATION.md`
- `PAYWALL_GATE_SUMMARY.md`
- `PAYWALL_GATE_TESTING_GUIDE.md`
- `PAYWALL_IMPLEMENTATION_ANALYSIS.md`
- `PAYWALL_IMPLEMENTATION_SUMMARY.md`
- `PAYWALL_NAVIGATION_TESTING.md`
- `PAYWALL_README.md`
- `PAYWALL_SUBSCRIPTION_UPDATE_REPORT.md`

### Subscription Documentation  
- `SUBSCRIPTION_ARCHITECTURE.md`
- `SUBSCRIPTION_INTEGRATION.md`
- `SUBSCRIPTION_QUICK_REFERENCE.md`
- `SUBSCRIPTION_README.md`
- `SUBSCRIPTION_TESTING_CHECKLIST.md`
- `SUBSCRIPTION_USAGE_GUIDE.md`
- `SUBSCRIPTION_WIRING_SUMMARY.md`

### Trial Documentation
- `TRIAL_PERIOD_GUIDE.md`

---

## What Replaced These Documents?

### ✅ Current Documentation (Active)

Located in parent `/docs` folder:

1. **GENEROUS_FREEMIUM_IMPLEMENTATION.md**
   - Complete guide to the new freemium model
   - Architecture, features, pricing strategy

2. **FREEMIUM_TESTING_GUIDE.md**
   - Testing scenarios for freemium model
   - No trial, no hard paywall testing

3. **OLD_SUBSCRIPTION_SYSTEM_REMOVAL.md**
   - What was removed from the codebase
   - Product ID mapping (old → new)
   - Migration guidance

4. **TRIAL_SYSTEM_REMOVAL.md**
   - Detailed trial removal documentation
   - Behavioral changes

5. **DOCUMENTATION_INDEX.md**
   - Complete index of all documentation
   - What's active, what's archived, what needs updating

---

## Key Differences: Old vs New

| Aspect | Old Model (REMOVED) | New Model (CURRENT) |
|--------|---------------------|---------------------|
| **Trial** | 7-day trial | No trial |
| **Core Features** | Locked after trial | Free forever |
| **Premium Features** | All features | Favorites, Highlights, Fonts |
| **Paywall** | Hard, non-dismissible | Soft, always dismissible |
| **Product IDs** | `yearly_subscription`, `onetime_purchase` | `support_basic`, `support_generous` |
| **Pricing** | $0.99/year, $2.50 one-time | GH₵ 15, GH₵ 20 one-time |
| **Messaging** | "Unlock Full Hymnal" | "Support this Ministry" |
| **Access Check** | `isInTrial \|\| isSubscribed` | `isSubscribed` only |
| **10-hymn prompt** | N/A | Natural interruption point |

---

## Migration Notes

### If You're Looking for Information...

**DON'T USE** these archived docs. Instead:

**Looking for**: Trial implementation  
**Use instead**: There is no trial - see [TRIAL_SYSTEM_REMOVAL.md](../TRIAL_SYSTEM_REMOVAL.md)

**Looking for**: Paywall implementation  
**Use instead**: Support sheet - see [GENEROUS_FREEMIUM_IMPLEMENTATION.md](../GENEROUS_FREEMIUM_IMPLEMENTATION.md)

**Looking for**: Subscription architecture  
**Use instead**: Freemium architecture - see [GENEROUS_FREEMIUM_IMPLEMENTATION.md](../GENEROUS_FREEMIUM_IMPLEMENTATION.md)

**Looking for**: Product IDs  
**Use instead**: `support_basic`, `support_generous` - see [OLD_SUBSCRIPTION_SYSTEM_REMOVAL.md](../OLD_SUBSCRIPTION_SYSTEM_REMOVAL.md)

**Looking for**: Testing guide  
**Use instead**: [FREEMIUM_TESTING_GUIDE.md](../FREEMIUM_TESTING_GUIDE.md)

---

## Code References That No Longer Exist

These code elements were **completely removed** from the codebase:

### Removed Constants
```kotlin
// ❌ REMOVED - Don't use
val YEARLY_SUBSCRIPTION = "yearly_subscription"
val ONETIME_PURCHASE = "onetime_purchase"
const val TRIAL_DURATION_DAYS = 7
```

### Removed States
```kotlin
// ❌ REMOVED - Don't use
enum class EntitlementState {
    TRIAL,           // Removed
    TRIAL_EXPIRED,   // Removed
    // ... others remain
}
```

### Removed Properties
```kotlin
// ❌ REMOVED - Don't use
val firstInstallDate: Long
val trialDaysRemaining: Int?
fun isTrialActive(): Boolean
fun getTrialDaysRemaining(): Int?
```

### Removed UI
```kotlin
// ❌ REMOVED - Don't use
TrialBanner(daysRemaining = ...)
```

---

## Historical Context

These documents represent approximately **3 months of work** (Oct-Dec 2025) on the trial-based subscription system. While that implementation worked technically, user research and cultural considerations led to a complete redesign for the Ghanaian market.

**Key Learning**: Technical implementation must align with cultural context and user behavior. The generous freemium model better serves the mission: "Keep our hymns alive."

---

## Can These Be Deleted?

**Recommendation**: Keep archived for 6-12 months for historical reference.

**Delete after**: June 2026 (if no issues arise with new model)

**Why keep temporarily**:
- Reference for understanding why certain decisions were made
- Historical context for team members
- Potential insights if revisiting pricing strategy
- Comparison data for A/B testing results

---

**For Current Documentation**: See [DOCUMENTATION_INDEX.md](../DOCUMENTATION_INDEX.md)  
**For Migration Guide**: See [OLD_SUBSCRIPTION_SYSTEM_REMOVAL.md](../OLD_SUBSCRIPTION_SYSTEM_REMOVAL.md)  
**For Freemium Model**: See [GENEROUS_FREEMIUM_IMPLEMENTATION.md](../GENEROUS_FREEMIUM_IMPLEMENTATION.md)

