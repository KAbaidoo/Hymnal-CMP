# Paywall Implementation - Complete Analysis

## Table of Contents
1. [SubscriptionManager](#subscriptionmanager)
2. [Paywall UI and Gating](#paywall-ui-and-gating)
3. [Billing Integration](#billing-integration)
4. [Settings/Storage](#settingsstorage)
5. [Edge Cases](#edge-cases)

---

## SubscriptionManager

### Trial Tracking

#### First Install Date
```kotlin
// Storage: SubscriptionStorage.kt
var firstInstallDate: Long
    get() = settings.getLong(KEY_FIRST_INSTALL_DATE, 0L)
    set(value) = settings.putLong(KEY_FIRST_INSTALL_DATE, value)

// Initialization (called on app startup)
fun initializeFirstInstallIfNeeded() {
    if (firstInstallDate == 0L) {
        firstInstallDate = Clock.System.now().toEpochMilliseconds()
    }
}
```

**Key Points:**
- Stored in platform-specific persistent storage (SharedPreferences/UserDefaults)
- Set once on first app launch using kotlinx.datetime
- Persists across app restarts and reinstalls
- Used as baseline for trial period calculation
- Cross-platform consistent time handling

#### 30-Day Window Calculation
```kotlin
fun getTrialDaysRemaining(): Int? {
    if (isSubscribed) return null
    if (firstInstallDate == 0L) return null
    
    val currentTime = Clock.System.now().toEpochMilliseconds()
    val daysSinceInstall = (currentTime - firstInstallDate) / MILLIS_PER_DAY
    val daysRemaining = TRIAL_DURATION_DAYS - daysSinceInstall.toInt()
    
    return if (daysRemaining > 0) daysRemaining else 0
}
```

**Algorithm:**
1. Get current time using kotlinx.datetime (cross-platform)
2. Calculate days elapsed since first install
3. Subtract from 30-day trial period
4. Return remaining days (min 0)

### Purchase/Entitlement State

#### EntitlementState Enum
```kotlin
enum class EntitlementState {
    TRIAL,                  // Within 30-day trial, has access
    SUBSCRIBED,            // Active purchase, has access
    TRIAL_EXPIRED,         // Trial ended, no access, needs paywall
    SUBSCRIPTION_EXPIRED,  // Subscription ended, no access, needs paywall
    NONE                   // No trial started, no access, needs paywall
}
```

#### State Determination Logic
```kotlin
fun getEntitlementState(): EntitlementState {
    val currentTime = Clock.System.now().toEpochMilliseconds()
    
    // Check subscription first
    if (isSubscribed) {
        // One-time purchases never expire
        if (purchaseType == PurchaseType.ONE_TIME_PURCHASE) {
            return EntitlementState.SUBSCRIBED
        }
        
        // For renewable subscriptions, check expiration
        expirationDate?.let { expiration ->
            if (currentTime > expiration) {
                return EntitlementState.SUBSCRIPTION_EXPIRED
            }
        }
        return EntitlementState.SUBSCRIBED
    }
    
    // Check trial
    if (firstInstallDate > 0) {
        val daysSinceInstall = (currentTime - firstInstallDate) / MILLIS_PER_DAY
        return if (daysSinceInstall < TRIAL_DURATION_DAYS) {
            EntitlementState.TRIAL
        } else {
            EntitlementState.TRIAL_EXPIRED
        }
    }
    
    return EntitlementState.NONE
}
```

**Decision Tree:**
1. Is user subscribed? 
   - Yes → Is it ONE_TIME_PURCHASE? → Always SUBSCRIBED (never expires)
   - Yes → Check expiration → SUBSCRIBED or SUBSCRIPTION_EXPIRED
   - No → Continue
2. Has first install date?
   - Yes → Check trial period → TRIAL or TRIAL_EXPIRED
   - No → NONE

### Restoration Logic

#### On Reinstall

**Android:**
```kotlin
override fun restorePurchases(callback: (Boolean) -> Unit) {
    billingHelper.checkSubscriptionStatus { isSubscribed ->
        if (isSubscribed) {
            storage.isSubscribed = true
            storage.lastVerificationTime = Clock.System.now().toEpochMilliseconds()
            refreshEntitlementState()
            callback(true)
        } else {
            callback(false)
        }
    }
}
```

**iOS:**
```swift
public func restorePurchases(callback: @escaping (KotlinBoolean) -> Void) {
    restoreCallBack = callback
    SKPaymentQueue.default().restoreCompletedTransactions()
}

public func paymentQueueRestoreCompletedTransactionsFinished(_ queue: SKPaymentQueue) {
    let yearlySubscribed = UserDefaults.standard.bool(forKey: YEARLY_SUBSCRIPTION_ID)
    let onetimePurchased = UserDefaults.standard.bool(forKey: ONETIME_PURCHASE_ID)
    
    let hasRestoredPurchases = yearlySubscribed || onetimePurchased
    restoreCallBack?(KotlinBoolean(value: hasRestoredPurchases))
    restoreCallBack = nil
}
```

**Constants:**
- `YEARLY_SUBSCRIPTION_ID = "yearly_subscription"`
- `ONETIME_PURCHASE_ID = "onetime_purchase"`

**Process:**
1. User clicks "Restore Purchases" button
2. Platform queries purchase history
3. Updates local storage if purchases found
4. Refreshes entitlement state
5. Callback invoked with success/failure

### Persistence

#### Data Stored
```kotlin
class SubscriptionStorage(private val settings: Settings) {
    var firstInstallDate: Long              // Trial start
    var purchaseDate: Long?                 // When user purchased
    var purchaseType: PurchaseType          // YEARLY, ONE_TIME, or NONE
    var productId: String?                  // Platform product ID
    var expirationDate: Long?               // For renewable subscriptions
    var isSubscribed: Boolean               // Current subscription status
    var lastVerificationTime: Long          // Last platform check
}
```

#### Recording Purchase
```kotlin
fun recordPurchase(
    productId: String,
    purchaseType: PurchaseType,
    purchaseTimestamp: Long = Clock.System.now().toEpochMilliseconds(),
    expirationTimestamp: Long? = null
) {
    this.productId = productId
    this.purchaseType = purchaseType
    this.purchaseDate = purchaseTimestamp
    this.expirationDate = expirationTimestamp
    this.isSubscribed = true
    this.lastVerificationTime = Clock.System.now().toEpochMilliseconds()
}
```

---

## Paywall UI and Gating

### Where Features Are Gated

#### PremiumFeatureGate Composable
```kotlin
@Composable
fun PremiumFeatureGate(
    premiumContent: @Composable () -> Unit,
    showPaywallOnDenied: Boolean = true,
    fallbackContent: @Composable (() -> Unit)? = null
) {
    val subscriptionManager: SubscriptionManager = koinInject()
    val entitlementInfo by subscriptionManager.entitlementState.collectAsState()
    
    if (entitlementInfo.hasAccess) {
        premiumContent()  // Show premium feature
    } else {
        if (showPaywallOnDenied) {
            navigator.push(PayWallScreen())  // Navigate to paywall
        } else {
            fallbackContent?.invoke()  // Show fallback
        }
    }
}
```

**Usage Examples:**
```kotlin
// Example 1: Premium hymn details
PremiumFeatureGate(
    premiumContent = { HymnDetailsWithNotes() }
)

// Example 2: Premium feature with fallback
PremiumFeatureGate(
    premiumContent = { AdvancedSearchScreen() },
    showPaywallOnDenied = false,
    fallbackContent = { BasicSearchScreen() }
)
```

### When Paywall Is Triggered

**Automatic Triggers:**
1. `PremiumFeatureGate` detects `hasAccess = false`
2. User tries to access gated feature
3. Navigation to `PayWallScreen()` occurs

**Manual Triggers:**
```kotlin
// From settings menu
navigator.push(PayWallScreen())

// After trial expiry notification
if (entitlementInfo.state == EntitlementState.TRIAL_EXPIRED) {
    showDialog("Trial ended", onConfirm = {
        navigator.push(PayWallScreen())
    })
}
```

### How Entitlements Unlock Access

#### Access Check
```kotlin
data class EntitlementInfo(...) {
    val hasAccess: Boolean
        get() = state == EntitlementState.TRIAL || 
                state == EntitlementState.SUBSCRIBED
}
```

**Flow:**
1. User makes purchase → `purchaseSubscription()` called
2. Platform confirms purchase → callback invoked
3. Storage updated → `recordPurchase()` called
4. State refreshed → `_entitlementState.value` updated
5. UI reacts → `collectAsState()` receives new value
6. Feature gate opens → `hasAccess` becomes `true`

#### Reactive UI Updates
```kotlin
// In SubscriptionManager
private val _entitlementState = MutableStateFlow(storage.getEntitlementInfo())
val entitlementState: StateFlow<EntitlementInfo> = _entitlementState.asStateFlow()

// Refresh after purchase
private fun refreshEntitlementState() {
    _entitlementState.value = storage.getEntitlementInfo()
}
```

**UI responds automatically:**
```kotlin
val entitlementInfo by subscriptionManager.entitlementState.collectAsState()

if (entitlementInfo.hasAccess) {
    // This updates automatically when state changes
    PremiumContent()
}
```

### Paywall UI Components

#### Trial Information Display
```kotlin
@Composable
private fun PaywallHeader(trialDaysRemaining: Int? = null) {
    // ... title text ...
    
    if (trialDaysRemaining != null && trialDaysRemaining > 0) {
        Text(
            text = "$trialDaysRemaining day${if (trialDaysRemaining != 1) "s" else ""} left in trial",
            style = MaterialTheme.typography.bodyMedium,
            color = YellowAccent,
            fontWeight = FontWeight.Bold
        )
    }
    
    // ... subtitle text ...
}
```

#### Restore Button
```kotlin
OutlinedButton(
    modifier = Modifier.fillMaxWidth().height(48.dp),
    enabled = !isLoading && !isRestoring,
    onClick = onRestore,
    shape = RoundedCornerShape(12.dp)
) {
    Text(
        text = if (isRestoring) "Restoring..." else "Restore Purchases",
        style = MaterialTheme.typography.bodyMedium
    )
}
```

#### Status Messages
```kotlin
if (errorMsg != null) {
    Text(
        text = errorMsg,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error
    )
}

if (successMsg != null) {
    Text(
        text = successMsg,
        style = MaterialTheme.typography.bodyMedium,
        color = Color(0xFF4CAF50)  // Green
    )
}
```

---

## Billing Integration

### Product IDs

#### Universal Product IDs (Cross-Platform)
Both platforms use the same product ID strings for consistency:

```kotlin
// iOS Constants (IosSubscriptionManager.kt)
const val YEARLY_SUBSCRIPTION_ID = "yearly_subscription"
const val ONETIME_PURCHASE_ID = "onetime_purchase"

// Android Constants (BillingHelper.kt)
val YEARLY_SUBSCRIPTION = "yearly_subscription"
val ONETIME_PURCHASE = "onetime_purchase"
```

#### Platform-Specific Product Types

**Android (Google Play):**
- `yearly_subscription` → Uses `BillingClient.ProductType.SUBS` (subscription)
- `onetime_purchase` → Uses `BillingClient.ProductType.INAPP` (in-app product/one-time)

**iOS (App Store):**
- `yearly_subscription` → Auto-renewable subscription
- `onetime_purchase` → Non-consumable purchase

#### Common Purchase Type Enum
```kotlin
enum class PurchaseType {
    NONE,
    YEARLY_SUBSCRIPTION,   // Renewable, can expire
    ONE_TIME_PURCHASE      // Never expires, lifetime access
}
```

#### One-Time Purchase Handling
The `onetime_purchase` product represents a **non-consumable one-time purchase** - users pay once and own it forever:
- iOS: Configure as **non-consumable** in App Store Connect
- Android: Configure as **in-app product** in Google Play Console (ProductType.INAPP)
- **Important**: One-time purchases never expire and grant lifetime access

The system correctly handles one-time purchases:
- No expiration date checking for `PurchaseType.ONE_TIME_PURCHASE`
- Remains in `SUBSCRIBED` state indefinitely
- Restored via platform mechanisms like any other purchase

### Purchase Flow

#### Android (Google Play Billing)
```kotlin
// From SubscriptionManager.android.kt
override fun purchaseSubscription(plan: PayPlan, callback: (Boolean) -> Unit) {
    val activity = this.activity ?: return
    
    val (productId, productType) = when (plan) {
        PayPlan.Yearly -> billingHelper.YEARLY_SUBSCRIPTION to BillingClient.ProductType.SUBS
        PayPlan.OneTime -> billingHelper.ONETIME_PURCHASE to BillingClient.ProductType.INAPP
    }
    
    billingHelper.purchaseProduct(productId, productType, activity) { success ->
        if (success) {
            val purchaseType = when (plan) {
                PayPlan.Yearly -> PurchaseType.YEARLY_SUBSCRIPTION
                PayPlan.OneTime -> PurchaseType.ONE_TIME_PURCHASE
            }
            storage.recordPurchase(
                productId = productId,
                purchaseType = purchaseType
            )
            refreshEntitlementState()
        }
        callback(success)
    }
}

// From BillingHelper.kt
fun purchaseProduct(productId: String, productType: String, activity: Activity, callback: (Boolean) -> Unit) {
    // 1. Connect to Play Store
    connectPlayStore { isConnected ->
        if (!isConnected) {
            callback(false)
            return@connectPlayStore
        }
        
        // 2. Query product details
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(productType)  // SUBS or INAPP
                    .build()
            ))
            .build()
        
        billingClient.queryProductDetailsAsync(params) { result, products ->
            // 3. Launch billing flow based on product type
            val productDetails = products.first()
            
            val productDetailsParamsBuilder = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
            
            // For subscriptions, need offer token
            if (productType == BillingClient.ProductType.SUBS) {
                val offerToken = productDetails.subscriptionOfferDetails?.first()?.offerToken
                productDetailsParamsBuilder.setOfferToken(offerToken!!)
            }
            
            val billingParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productDetailsParamsBuilder.build()))
                .build()
            
            purchaseCallback = callback
            billingClient.launchBillingFlow(activity, billingParams)
        }
    }
}
```

**Key Points:**
- Supports both subscription (SUBS) and one-time (INAPP) products
- Product type determines billing flow parameters
- Subscriptions require offer token, one-time purchases don't
- Maps PayPlan to both productId and productType correctly

**Lifecycle:**
1. Connect to billing client
2. Query product details with correct product type
3. Build billing flow params (add offer token for SUBS)
4. Launch platform purchase UI
5. Handle purchase result in listener
6. Acknowledge purchase
7. Record purchase in storage
8. Invoke callback

#### iOS (StoreKit)
```swift
public func purchaseSubscription(productId: String, callback: @escaping (KotlinBoolean) -> Void) -> Bool {
    guard let product = products.first(where: { $0.productIdentifier == productId }) else {
        callback(KotlinBoolean(value: false))
        return false
    }
    
    let payment = SKPayment(product: product)
    purchaseCallBack = callback
    SKPaymentQueue.default().add(payment)
    return true
}
```

**Transaction Observer:**
```swift
public func paymentQueue(_ queue: SKPaymentQueue, updatedTransactions transactions: [SKPaymentTransaction]) {
    for transaction in transactions {
        switch transaction.transactionState {
        case .purchased:
            unlockContent(productId: transaction.payment.productIdentifier)
            SKPaymentQueue.default().finishTransaction(transaction)
        case .restored:
            unlockContent(productId: transaction.payment.productIdentifier)
            SKPaymentQueue.default().finishTransaction(transaction)
        case .failed:
            purchaseCallBack?(KotlinBoolean(value: false))
            SKPaymentQueue.default().finishTransaction(transaction)
        default:
            break
        }
    }
}
```

### Receipt/Entitlement Verification

#### Android
```kotlin
private fun handlePurchase(purchases: List<Purchase>) {
    for (purchase in purchases) {
        when (purchase.purchaseState) {
            Purchase.PurchaseState.PURCHASED -> {
                Log.d(TAG, "Subscription is active: ${purchase.products}")
                acknowledgePurchase(purchase)
                purchaseCallback?.invoke(true)
            }
            Purchase.PurchaseState.PENDING -> {
                Log.d(TAG, "Purchase is pending")
                // Don't invoke callback - wait for final state
            }
            Purchase.PurchaseState.UNSPECIFIED_STATE -> {
                purchaseCallback?.invoke(false)
            }
        }
    }
}

private fun acknowledgePurchase(purchase: Purchase) {
    if (!purchase.isAcknowledged) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        
        billingClient.acknowledgePurchase(params) { result ->
            // Purchase acknowledged
        }
    }
}
```

**Verification:**
- Google Play automatically verifies purchases
- `queryPurchasesAsync` retrieves verified purchases
- Purchase token used for acknowledgement
- Server-side verification not currently implemented

#### iOS
```swift
private func unlockContent(productId: String) {
    purchaseCallBack?(KotlinBoolean(value: true))
    UserDefaults.standard.set(true, forKey: productId)
}
```

**Verification:**
- StoreKit provides transaction verification
- Receipt stored in app bundle
- UserDefaults used for quick access
- Server-side verification not currently implemented

### Restore Purchases

#### Button in UI
```kotlin
OutlinedButton(
    onClick = {
        subscriptionManager.restorePurchases { success ->
            if (success) {
                successMessage = "Purchases restored successfully!"
            } else {
                purchaseError = "No purchases found to restore."
            }
        }
    }
) {
    Text("Restore Purchases")
}
```

#### Android Restoration
```kotlin
override fun restorePurchases(callback: (Boolean) -> Unit) {
    billingHelper.checkSubscriptionStatus { isSubscribed ->
        if (isSubscribed) {
            storage.isSubscribed = true
            storage.lastVerificationTime = System.currentTimeMillis()
            refreshEntitlementState()
            callback(true)
        } else {
            callback(false)
        }
    }
}
```

**Note:** Android automatically restores purchases via `queryPurchasesAsync`, so explicit restore is mainly for UI feedback.

#### iOS Restoration
```swift
public func restorePurchases(callback: @escaping (KotlinBoolean) -> Void) {
    restoreCallBack = callback
    SKPaymentQueue.default().restoreCompletedTransactions()
}
```

**Requires:** User must be signed into same Apple ID

### Cross-Platform Handling

#### Consistent Data Model
```kotlin
data class EntitlementInfo(
    val state: EntitlementState,
    val purchaseType: PurchaseType,
    val trialDaysRemaining: Int?,
    val firstInstallDate: Long?,
    val purchaseDate: Long?,
    val expirationDate: Long?
)
```

**Same structure on both platforms**

#### Platform-Specific Differences

| Aspect | Android | iOS |
|--------|---------|-----|
| **Storage** | SharedPreferences | UserDefaults |
| **Product IDs** | `premium_subscription` | `yearly_subscription`, `onetime_purchase` |
| **Restore** | Automatic via query | Manual via restore API |
| **Verification** | Google Play verifies | StoreKit verifies |
| **Pending Purchases** | Supported | Not applicable |

#### Abstraction Layer
```kotlin
interface SubscriptionManager {
    fun purchaseSubscription(plan: PayPlan, callback: (Boolean) -> Unit)
    fun isUserSubscribed(callback: (Boolean) -> Unit)
    fun restorePurchases(callback: (Boolean) -> Unit)
    fun getEntitlementInfo(): EntitlementInfo
    val entitlementState: StateFlow<EntitlementInfo>
}
```

**Both platforms implement same interface**

---

## Settings/Storage

### Trial and Purchase State Storage

#### Settings Library
```kotlin
// Using russhwolf/multiplatform-settings
implementation(libs.multiplatform.settings)
implementation(libs.multiplatform.settings.noargs)
```

**Provides:**
- Cross-platform key-value storage
- Backed by SharedPreferences (Android) and UserDefaults (iOS)
- Type-safe accessors

#### Storage Keys and Values
```kotlin
private const val KEY_FIRST_INSTALL_DATE = "subscription_first_install_date"
private const val KEY_PURCHASE_DATE = "subscription_purchase_date"
private const val KEY_PURCHASE_TYPE = "subscription_purchase_type"
private const val KEY_PRODUCT_ID = "subscription_product_id"
private const val KEY_EXPIRATION_DATE = "subscription_expiration_date"
private const val KEY_IS_SUBSCRIBED = "subscription_is_subscribed"
private const val KEY_LAST_VERIFICATION_TIME = "subscription_last_verification_time"
```

**Data Types:**
| Key | Type | Purpose |
|-----|------|---------|
| `firstInstallDate` | Long | Trial start timestamp |
| `purchaseDate` | Long? | Purchase timestamp |
| `purchaseType` | String (enum) | YEARLY, ONE_TIME, NONE |
| `productId` | String? | Platform product ID |
| `expirationDate` | Long? | Subscription expiry |
| `isSubscribed` | Boolean | Current status |
| `lastVerificationTime` | Long | Last platform check |

#### Example Storage Access
```kotlin
var firstInstallDate: Long
    get() = settings.getLong(KEY_FIRST_INSTALL_DATE, 0L)
    set(value) = settings.putLong(KEY_FIRST_INSTALL_DATE, value)

var purchaseType: PurchaseType
    get() {
        val value = settings.getString(KEY_PURCHASE_TYPE, PurchaseType.NONE.name)
        return try {
            PurchaseType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            PurchaseType.NONE
        }
    }
    set(value) = settings.putString(KEY_PURCHASE_TYPE, value.name)
```

### Synchronization

#### StateFlow for Reactive Updates
```kotlin
private val _entitlementState = MutableStateFlow(storage.getEntitlementInfo())
val entitlementState: StateFlow<EntitlementInfo> = _entitlementState.asStateFlow()

private fun refreshEntitlementState() {
    _entitlementState.value = storage.getEntitlementInfo()
}
```

**UI observes changes:**
```kotlin
val entitlementInfo by subscriptionManager.entitlementState.collectAsState()

// UI automatically updates when state changes
Text("Trial: ${entitlementInfo.trialDaysRemaining} days")
```

#### Synchronization Points
1. **App Startup** → `initialize()` checks platform and updates state
2. **After Purchase** → `recordPurchase()` updates storage and emits new state
3. **After Restore** → Platform query updates storage and emits new state
4. **Periodic Checks** → Can call `isUserSubscribed()` to refresh

#### Cross-Device Sync
- **Handled by platform** (Google Play/App Store)
- Each device queries platform for current entitlements
- Local storage updated with platform truth
- Calling `restorePurchases()` syncs across devices

---

## Edge Cases

### Device Clock Changes

#### Detection
```kotlin
// Compare current time against last known good time
val currentTime = System.currentTimeMillis()
val lastCheck = storage.lastVerificationTime

if (currentTime < lastCheck) {
    // Clock moved backward - suspicious
}

if (currentTime < firstInstallDate) {
    // Impossible - clock definitely wrong
}
```

#### Mitigation
1. **Use relative time** when possible
2. **Validate against known events**
   ```kotlin
   if (currentTime < firstInstallDate || currentTime < purchaseDate) {
       // Clock is wrong, use last known state
   }
   ```
3. **Re-verify with platform** to get authoritative state
   ```kotlin
   subscriptionManager.isUserSubscribed { isSubscribed ->
       // Platform provides clock-independent truth
   }
   ```
4. **Grace period** for expired subscriptions
   ```kotlin
   // Allow 24-hour grace period for clock issues
   if (daysSinceExpiration < 1) {
       // Still allow access
   }
   ```

### Reinstall

#### Scenario 1: Reinstall During Trial
```kotlin
// Before uninstall
firstInstallDate = 1234567890
trialDaysRemaining = 15

// After reinstall
firstInstallDate = 1234567890  // Preserved
trialDaysRemaining = 13  // Calculated from preserved date
```

**Result:** Trial continues from where it left off

#### Scenario 2: Reinstall After Trial Expired
```kotlin
// Before uninstall
firstInstallDate = 1234567890  // 40 days ago
trialDaysRemaining = 0

// After reinstall  
firstInstallDate = 1234567890  // Preserved
state = TRIAL_EXPIRED
```

**Result:** User must purchase to access features

#### Scenario 3: Reinstall With Active Subscription
```kotlin
// After reinstall
storage.isSubscribed = false  // Local cache empty

// User clicks "Restore Purchases"
restorePurchases { success ->
    // Queries platform
    // Finds active subscription
    // Updates local storage
    storage.isSubscribed = true
}
```

**Result:** Subscription restored via platform

### Multiple Devices

#### Trial Period
- **Independent trials** on each device
- Each has own `firstInstallDate`
- User gets 30 days on Device A, 30 days on Device B

#### Purchases
- **Shared via platform** (Google/Apple account)
- Purchase on Device A → Available on Device B
- Requires calling `restorePurchases()` or automatic platform sync

#### Example Flow
```kotlin
// Device A
purchaseSubscription(PayPlan.Yearly) { success ->
    // Purchase successful
}

// Device B (later)
initialize()  // May auto-detect purchase
// OR
restorePurchases()  // Explicitly restore
```

### Offline Behavior

#### Trial Calculation (Works Offline)
```kotlin
// No network needed
val daysRemaining = (currentTime - firstInstallDate) / MILLIS_PER_DAY
```

#### Subscription Status (Uses Cache)
```kotlin
val isSubscribed = storage.isSubscribed  // Cached value
val lastCheck = storage.lastVerificationTime

if (currentTime - lastCheck > ONE_DAY) {
    // Cache might be stale, but still usable offline
}
```

#### Grace Period for Offline
```kotlin
// Allow 3 days offline before blocking access
val daysSinceLastVerification = (currentTime - lastVerificationTime) / MILLIS_PER_DAY

if (storage.isSubscribed && daysSinceLastVerification < 3) {
    // Allow access even if can't verify online
    return EntitlementInfo(state = EntitlementState.SUBSCRIBED, ...)
}
```

#### When Online Returns
```kotlin
initialize()  // Refreshes from platform
isUserSubscribed { isSubscribed ->
    // Updates cache with current state
    storage.lastVerificationTime = System.currentTimeMillis()
}
```

#### Queue Operations
**Not currently implemented**, but could add:
```kotlin
class PendingOperationQueue {
    fun queuePurchase(plan: PayPlan)
    fun queueRestore()
    
    fun processQueue() {
        // When network returns, process queued operations
    }
}
```

---

## Summary

### Complete Data Flow

1. **First Launch**
   - `initialize()` called
   - `firstInstallDate` set
   - State: `TRIAL` (30 days)

2. **During Trial**
   - User accesses premium features
   - Trial countdown shown
   - State remains `TRIAL`

3. **Trial Expires**
   - State changes to `TRIAL_EXPIRED`
   - Features gated
   - Paywall shown

4. **User Purchases**
   - Payment processed via platform
   - `recordPurchase()` updates storage
   - State changes to `SUBSCRIBED`
   - Features unlocked

5. **User Reinstalls**
   - `firstInstallDate` preserved
   - `isSubscribed` initially false
   - User clicks "Restore Purchases"
   - Platform verifies purchase
   - State restored to `SUBSCRIBED`

### Key Design Principles

1. **Platform as Source of Truth**
   - Google Play and App Store provide authoritative purchase state
   - Local storage is cache only

2. **Reactive UI**
   - StateFlow emits state changes
   - UI observes and updates automatically

3. **Graceful Degradation**
   - Works offline with cached data
   - Grace periods for verification failures

4. **Security**
   - Purchases verified by platform
   - Clock manipulation mitigated
   - Server-side verification recommended for production

5. **User-Friendly**
   - Trial period clearly displayed
   - Restore purchases always available
   - Clear error messages
