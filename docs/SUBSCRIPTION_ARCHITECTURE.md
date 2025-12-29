# Subscription Module Architecture (Updated)

**Latest Changes:**
- ✅ Trial period tracking (30-day window)
- ✅ Entitlement state management
- ✅ Restore purchases functionality
- ✅ Persistent storage for trial and purchase data
- ✅ StateFlow for reactive UI updates
- ✅ Feature gating composables

**See Also:**
- [Trial Period Guide](./TRIAL_PERIOD_GUIDE.md) - Complete trial implementation details
- [Paywall Implementation Analysis](./PAYWALL_IMPLEMENTATION_ANALYSIS.md) - Comprehensive analysis
- [Feature Gating Usage Guide](./FEATURE_GATING_USAGE_GUIDE.md) - Code examples for developers

```
┌─────────────────────────────────────────────────────────────────┐
│                         UI Layer (Common)                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────────┐         ┌──────────────────┐              │
│  │  PayWallScreen   │────────▶│  PayWallContent  │              │
│  │  (Voyager Screen)│         │  (Composable UI) │              │
│  └──────────────────┘         └──────────────────┘              │
│           │                             │                        │
│           │                             │                        │
│           ▼                             ▼                        │
│    ┌──────────────────────────────────────┐                     │
│    │      onPurchase(plan: PayPlan)       │                     │
│    │      onRestore() ← NEW               │                     │
│    │      trialDaysRemaining ← NEW        │                     │
│    └──────────────────────────────────────┘                     │
│                      │                                           │
│           ┌──────────┴──────────┐                               │
│           │                     │                               │
│  ┌────────▼─────────┐  ┌───────▼──────────┐                   │
│  │ PremiumFeature   │  │ CheckPremium     │  ← NEW             │
│  │ Gate             │  │ Access           │                     │
│  └──────────────────┘  └──────────────────┘                     │
│                                                                   │
└──────────────────────┼───────────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Business Logic (Common)                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│         ┌────────────────────────────────────┐                  │
│         │   SubscriptionManager Interface     │                  │
│         ├────────────────────────────────────┤                  │
│         │ + purchaseSubscription(plan, cb)   │                  │
│         │ + isUserSubscribed(cb)             │                  │
│         │ + manageSubscription()             │                  │
│         │ + restorePurchases(cb)       ← NEW │                  │
│         │ + getEntitlementInfo()       ← NEW │                  │
│         │ + initialize()               ← NEW │                  │
│         │ + entitlementState: StateFlow← NEW │                  │
│         └────────────────────────────────────┘                  │
│                      ▲                                           │
│                      │                                           │
│         ┌────────────┴────────────┐                             │
│         │                         │                             │
│    ┌────▼──────────┐    ┌────────▼─────────┐                   │
│    │Subscription   │    │ Entitlement      │  ← NEW            │
│    │Storage        │    │ State            │                    │
│    └───────────────┘    └──────────────────┘                    │
│    - firstInstallDate   - TRIAL                                 │
│    - purchaseDate       - SUBSCRIBED                            │
│    - purchaseType       - TRIAL_EXPIRED                         │
│    - isSubscribed       - SUBSCRIPTION_EXPIRED                  │
│    - expirationDate     - NONE                                  │
│                                                                   │
└──────────────────────┼───────────────────────────────────────────┘
                       │
         ┌─────────────┴─────────────┐
         │                           │
         ▼                           ▼
┌────────────────────┐     ┌────────────────────┐
│   iOS Platform     │     │  Android Platform  │
├────────────────────┤     ├────────────────────┤
│                    │     │                    │
│ IosSubscription    │     │ AndroidSubscription│
│ Manager            │     │ Manager            │
│ + Storage          │     │ + Storage          │
│                    │     │                    │
│ Maps PayPlan to:   │     │ Uses BillingHelper │
│ - ios_yearly_      │     │                    │
│   subscription     │     │ Product ID:        │
│ - ios_onetime_     │     │ - premium_         │
│   subscription     │     │   subscription     │
│                    │     │                    │
│        │           │     │        │           │
│        ▼           │     │        ▼           │
│                    │     │                    │
│ Native             │     │ BillingHelper      │
│ SubscriptionProvider│    │ (Google Play       │
│ (Bridge)           │     │  Billing)          │
│        │           │     │                    │
│        ▼           │     │                    │
│                    │     │                    │
│ IosSubscription    │     └────────────────────┘
│ Provider.swift     │              │
│                    │              ▼
│ - StoreKit         │     ┌────────────────────┐
│ - SKPayment        │     │  Google Play       │
│ - SKTransaction    │     │  Billing Library   │
│ - UserDefaults     │     └────────────────────┘
│                    │
└────────────────────┘
         │
         ▼
┌────────────────────┐
│   App Store        │
│   StoreKit         │
└────────────────────┘


┌─────────────────────────────────────────────────────────────────┐
│                      Dependency Injection                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  iOS (MainViewController.kt):                                    │
│  startKoin {                                                     │
│    modules(... subscriptionModule)                               │
│  }                                                               │
│                                                                   │
│  subscriptionModule = module {                                   │
│    single<SubscriptionManager> { IosSubscriptionManager() }     │
│  }                                                               │
│                                                                   │
│  Android (MainActivity.kt):                                      │
│  startKoin {                                                     │
│    modules(... subscriptionModule)                               │
│  }                                                               │
│                                                                   │
│  subscriptionModule = module {                                   │
│    single { BillingHelper(androidContext()) }                   │
│    single<SubscriptionManager> {                                │
│      AndroidSubscriptionManager(androidContext(), get())        │
│    }                                                             │
│  }                                                               │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────┐
│                        Data Flow                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  1. User Opens PayWall                                           │
│     └─▶ PayWallScreen displayed                                 │
│         └─▶ Shows Yearly/OneTime options                        │
│                                                                   │
│  2. User Selects Plan & Clicks Purchase                          │
│     └─▶ onPurchase(plan) callback triggered                     │
│         └─▶ SubscriptionManager.purchaseSubscription(plan, cb)  │
│             │                                                    │
│             ├─▶ iOS: Maps to product ID                         │
│             │   └─▶ Calls StoreKit                              │
│             │       └─▶ Shows Apple payment sheet               │
│             │                                                    │
│             └─▶ Android: Calls BillingHelper                    │
│                 └─▶ Google Play Billing                         │
│                     └─▶ Shows Google payment sheet              │
│                                                                   │
│  3. Platform Processes Purchase                                  │
│     └─▶ Transaction completed/failed                            │
│         └─▶ Callback invoked: callback(success: Boolean)        │
│             │                                                    │
│             ├─▶ Success: Save to persistence                    │
│             │   - iOS: UserDefaults                             │
│             │   - Android: Google Play manages                  │
│             │                                                    │
│             └─▶ PayWallScreen handles result                    │
│                 └─▶ Success: navigator.pop()                    │
│                 └─▶ Failure: Show error message                 │
│                                                                   │
│  4. Check Subscription Status (Anywhere in App)                  │
│     └─▶ SubscriptionManager.isUserSubscribed(cb)                │
│         └─▶ Returns Boolean in callback                         │
│             └─▶ Enable/disable premium features                 │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
```

## Key Components

### 1. PayWallScreen (Common)
- Voyager Screen wrapper
- Manages purchase state (loading, errors)
- Handles navigation after purchase

### 2. PayWallContent (Common)
- Reusable Composable UI
- Displays subscription plans
- Accepts external state for flexibility

### 3. SubscriptionManager Interface (Common)
- Platform-agnostic interface
- Single source of truth for subscription logic
- Injected via Koin

### 4. Platform Implementations
- **iOS**: Uses StoreKit, bridges to Swift
- **Android**: Uses Google Play Billing Library

### 5. Dependency Injection
- Koin modules per platform
- Single manager instance per app lifecycle

## Product ID Mapping

| PayPlan  | iOS Product ID              | Android Product ID    |
|----------|-----------------------------|-----------------------|
| Yearly   | ios_yearly_subscription     | premium_subscription  |
| OneTime  | ios_onetime_subscription    | premium_subscription  |

*Note: Android currently uses same product ID for both plans. This can be changed in BillingHelper to support multiple products.*

## State Management

### Purchase Flow States
1. **Idle**: No purchase in progress
2. **Processing**: `isProcessing = true`, button disabled
3. **Success**: Navigate back, update subscription status
4. **Error**: Show error message, allow retry

### Subscription Status
- Checked on app launch
- Cached in platform-specific storage
- Can be refreshed anytime via `isUserSubscribed()`

