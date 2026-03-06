# Feature Gating Usage Guide

## Quick Start

### 1. Basic Feature Gate

Gate a premium feature with automatic paywall:

```kotlin
@Composable
fun MyScreen() {
    PremiumFeatureGate(
        premiumContent = {
            // This content only shows if user has access
            PremiumHymnNotesScreen()
        }
    )
}
```

**What happens:**
- User has access (trial or subscribed) → Shows `PremiumHymnNotesScreen()`
- User doesn't have access → Navigates to `PayWallScreen()`

### 2. Feature Gate with Fallback

Show different content instead of paywall:

```kotlin
@Composable
fun SearchScreen() {
    PremiumFeatureGate(
        premiumContent = {
            AdvancedSearchScreen()
        },
        showPaywallOnDenied = false,
        fallbackContent = {
            BasicSearchScreen()
        }
    )
}
```

**What happens:**
- User has access → Shows `AdvancedSearchScreen()`
- User doesn't have access → Shows `BasicSearchScreen()`

### 3. Conditional UI Elements

Show/hide UI elements based on subscription:

```kotlin
@Composable
fun HymnDetailScreen() {
    val subscriptionManager: SubscriptionManager = koinInject()
    val entitlementInfo by subscriptionManager.entitlementState.collectAsState()
    
    Column {
        HymnTitle()
        HymnLyrics()
        
        if (entitlementInfo.hasAccess) {
            // Premium features
            HymnNotes()
            HymnHistory()
            ShareButton()
        } else {
            // Upgrade prompt
            UpgradeButton(
                onClick = { navigator.push(PayWallScreen()) }
            )
        }
    }
}
```

### 4. Show Trial Banner

Display trial information to user:

```kotlin
@Composable
fun HomeScreen() {
    CheckPremiumAccess(
        onHasAccess = { entitlementInfo ->
            Column {
                // Show trial banner if in trial
                if (entitlementInfo.isInTrial) {
                    TrialBanner(
                        daysRemaining = entitlementInfo.trialDaysRemaining ?: 0,
                        onUpgrade = { navigator.push(PayWallScreen()) }
                    )
                }
                
                // Main content
                HymnList()
            }
        },
        onNoAccess = { entitlementInfo ->
            // Trial expired, show limited content
            Column {
                ExpiredBanner(
                    onClick = { navigator.push(PayWallScreen()) }
                )
                LimitedHymnList()
            }
        }
    )
}

@Composable
fun TrialBanner(daysRemaining: Int, onUpgrade: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF9C4)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Trial: $daysRemaining day${if (daysRemaining != 1) "s" else ""} left",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Upgrade to continue enjoying premium features",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Button(onClick = onUpgrade) {
                Text("Upgrade")
            }
        }
    }
}
```

## Advanced Usage

### 5. Different Features, Different Gates

```kotlin
@Composable
fun HymnDetailScreen(hymnId: Int) {
    val entitlementInfo by koinInject<SubscriptionManager>()
        .entitlementState.collectAsState()
    
    Column {
        // Basic features - always available
        HymnTitle(hymnId)
        HymnLyrics(hymnId)
        
        // Feature 1: Notes (requires access)
        if (entitlementInfo.hasAccess) {
            HymnNotes(hymnId)
        } else {
            LockedFeatureCard(
                featureName = "Personal Notes",
                onClick = { navigator.push(PayWallScreen()) }
            )
        }
        
        // Feature 2: Audio (requires access)
        if (entitlementInfo.hasAccess) {
            AudioPlayer(hymnId)
        } else {
            LockedFeatureCard(
                featureName = "Audio Playback",
                onClick = { navigator.push(PayWallScreen()) }
            )
        }
        
        // Feature 3: Share (always available)
        ShareButton(hymnId)
    }
}

@Composable
fun LockedFeatureCard(featureName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    featureName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Available with premium",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Upgrade"
            )
        }
    }
}
```

### 6. Trial Expiry Warning

Show warning when trial is ending soon:

```kotlin
@Composable
fun HomeScreen() {
    val entitlementInfo by koinInject<SubscriptionManager>()
        .entitlementState.collectAsState()
    
    LaunchedEffect(entitlementInfo) {
        // Show warning when 3 days left
        if (entitlementInfo.isInTrial && 
            entitlementInfo.trialDaysRemaining?.let { it <= 3 } == true) {
            showTrialEndingDialog()
        }
    }
    
    Column {
        // Content
    }
}

fun showTrialEndingDialog() {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Trial Ending Soon") },
        text = {
            Text(
                "Your trial ends in ${entitlementInfo.trialDaysRemaining} days. " +
                "Upgrade now to keep premium features."
            )
        },
        confirmButton = {
            Button(onClick = { navigator.push(PayWallScreen()) }) {
                Text("Upgrade Now")
            }
        },
        dismissButton = {
            TextButton(onClick = { /* dismiss */ }) {
                Text("Later")
            }
        }
    )
}
```

### 7. Settings Screen Integration

Show subscription status in settings:

```kotlin
@Composable
fun SettingsScreen() {
    val subscriptionManager: SubscriptionManager = koinInject()
    val entitlementInfo by subscriptionManager.entitlementState.collectAsState()
    
    Column {
        // Subscription Status Section
        SettingsSection(title = "Subscription") {
            when (entitlementInfo.state) {
                EntitlementState.TRIAL -> {
                    SubscriptionStatusCard(
                        title = "Free Trial",
                        description = "${entitlementInfo.trialDaysRemaining} days remaining",
                        action = "Upgrade",
                        onActionClick = { navigator.push(PayWallScreen()) }
                    )
                }
                EntitlementState.SUBSCRIBED -> {
                    SubscriptionStatusCard(
                        title = "Premium Active",
                        description = when (entitlementInfo.purchaseType) {
                            PurchaseType.YEARLY_SUBSCRIPTION -> "Yearly subscription"
                            PurchaseType.ONE_TIME_PURCHASE -> "Lifetime access"
                            else -> "Active"
                        },
                        action = "Manage",
                        onActionClick = { subscriptionManager.manageSubscription() }
                    )
                }
                EntitlementState.TRIAL_EXPIRED,
                EntitlementState.SUBSCRIPTION_EXPIRED,
                EntitlementState.NONE -> {
                    SubscriptionStatusCard(
                        title = "Free Version",
                        description = "Upgrade to unlock all features",
                        action = "Upgrade",
                        onActionClick = { navigator.push(PayWallScreen()) }
                    )
                }
            }
            
            // Restore purchases button
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    subscriptionManager.restorePurchases { success ->
                        if (success) {
                            showToast("Purchases restored!")
                        } else {
                            showToast("No purchases found")
                        }
                    }
                }
            ) {
                Text("Restore Purchases")
            }
        }
        
        // Other settings
    }
}
```

### 8. Analytics Integration

Track subscription events:

```kotlin
class SubscriptionAnalytics(
    private val subscriptionManager: SubscriptionManager,
    private val analytics: Analytics
) {
    
    init {
        // Observe state changes
        subscriptionManager.entitlementState
            .onEach { info ->
                when (info.state) {
                    EntitlementState.TRIAL -> {
                        analytics.logEvent("trial_started", mapOf(
                            "days_remaining" to info.trialDaysRemaining
                        ))
                    }
                    EntitlementState.SUBSCRIBED -> {
                        analytics.logEvent("subscription_active", mapOf(
                            "purchase_type" to info.purchaseType.name
                        ))
                    }
                    EntitlementState.TRIAL_EXPIRED -> {
                        analytics.logEvent("trial_expired")
                    }
                    else -> {}
                }
            }
            .launchIn(scope)
    }
    
    fun trackPaywallView() {
        val info = subscriptionManager.getEntitlementInfo()
        analytics.logEvent("paywall_viewed", mapOf(
            "source_state" to info.state.name,
            "trial_days_remaining" to info.trialDaysRemaining
        ))
    }
    
    fun trackPurchaseAttempt(plan: PayPlan) {
        analytics.logEvent("purchase_attempted", mapOf(
            "plan" to plan.name
        ))
    }
    
    fun trackPurchaseSuccess(plan: PayPlan) {
        analytics.logEvent("purchase_completed", mapOf(
            "plan" to plan.name
        ))
    }
}
```

### 9. Deep Linking to Paywall

Handle deep links that require premium:

```kotlin
fun handleDeepLink(uri: Uri) {
    when (uri.path) {
        "/premium-hymn" -> {
            val hymnId = uri.getQueryParameter("id")?.toIntOrNull()
            if (hymnId != null) {
                // Check access before navigating
                subscriptionManager.getEntitlementInfo().let { info ->
                    if (info.hasAccess) {
                        navigator.push(HymnDetailScreen(hymnId))
                    } else {
                        // Show paywall, then navigate if purchased
                        navigator.push(PayWallScreen())
                        // TODO: Add callback to navigate to hymn after purchase
                    }
                }
            }
        }
    }
}
```

### 10. Testing Helpers

Utilities for testing subscription flows:

```kotlin
class SubscriptionTestHelper(
    private val storage: SubscriptionStorage
) {
    
    fun setupActiveTrial() {
        storage.firstInstallDate = System.currentTimeMillis()
        storage.isSubscribed = false
    }
    
    fun setupExpiredTrial() {
        val fortyDaysAgo = System.currentTimeMillis() - 
            (40 * SubscriptionStorage.MILLIS_PER_DAY)
        storage.firstInstallDate = fortyDaysAgo
        storage.isSubscribed = false
    }
    
    fun setupActiveSubscription(type: PurchaseType = PurchaseType.YEARLY_SUBSCRIPTION) {
        storage.recordPurchase(
            productId = "test_product",
            purchaseType = type,
            purchaseTimestamp = System.currentTimeMillis()
        )
    }
    
    fun setupExpiredSubscription() {
        val yesterday = System.currentTimeMillis() - SubscriptionStorage.MILLIS_PER_DAY
        storage.recordPurchase(
            productId = "test_product",
            purchaseType = PurchaseType.YEARLY_SUBSCRIPTION,
            expirationTimestamp = yesterday
        )
    }
    
    fun reset() {
        storage.clearAll()
    }
}
```

## Best Practices

### 1. Always Initialize

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val subscriptionManager: SubscriptionManager = koin.get()
        subscriptionManager.initialize()
    }
}
```

### 2. Use Reactive State

```kotlin
// ✅ Good - Reactive
val entitlementInfo by subscriptionManager.entitlementState.collectAsState()
if (entitlementInfo.hasAccess) { /* ... */ }

// ❌ Bad - One-time check
val hasAccess = subscriptionManager.getEntitlementInfo().hasAccess
if (hasAccess) { /* ... */ }
```

### 3. Handle Loading States

```kotlin
@Composable
fun PurchaseButton() {
    var isProcessing by remember { mutableStateOf(false) }
    
    Button(
        enabled = !isProcessing,
        onClick = {
            isProcessing = true
            subscriptionManager.purchaseSubscription(plan) { success ->
                isProcessing = false
                // Handle result
            }
        }
    ) {
        Text(if (isProcessing) "Processing..." else "Purchase")
    }
}
```

### 4. Provide Feedback

```kotlin
subscriptionManager.restorePurchases { success ->
    if (success) {
        showSnackbar("Purchases restored successfully!")
    } else {
        showDialog(
            title = "No Purchases Found",
            message = "We couldn't find any purchases for this account. " +
                      "Make sure you're signed in with the same account you used to purchase."
        )
    }
}
```

### 5. Test Edge Cases

- Fresh install
- Reinstall during trial
- Reinstall after trial expired
- Reinstall with active subscription
- Clock changes
- Offline usage
- Expired subscription renewal

## Common Patterns

### Pattern 1: Soft Gate (Show Teaser)

```kotlin
@Composable
fun HymnNotes() {
    if (entitlementInfo.hasAccess) {
        FullNotes()
    } else {
        Column {
            TeaserNotes()  // Show first few notes
            UpgradePrompt()
        }
    }
}
```

### Pattern 2: Hard Gate (Block Completely)

```kotlin
@Composable
fun AudioPlayer() {
    PremiumFeatureGate(
        premiumContent = { FullAudioPlayer() }
    )
}
```

### Pattern 3: Trial Banner

```kotlin
@Composable
fun Screen() {
    if (entitlementInfo.isInTrial) {
        TrialCountdownBanner()
    }
    Content()
}
```

### Pattern 4: Feature Limit

```kotlin
@Composable
fun BookmarksList() {
    val bookmarks = getBookmarks()
    val limit = if (entitlementInfo.hasAccess) Int.MAX_VALUE else 5
    
    LazyColumn {
        items(bookmarks.take(limit)) { bookmark ->
            BookmarkItem(bookmark)
        }
        
        if (!entitlementInfo.hasAccess && bookmarks.size > limit) {
            item {
                UpgradeCard("Unlock unlimited bookmarks")
            }
        }
    }
}
```
