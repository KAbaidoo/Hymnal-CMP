# Using the Subscription Module

## Quick Start

### 1. Navigate to PayWall

From any screen in your app, navigate to the PayWall using Voyager:

```kotlin
import cafe.adriel.voyager.navigator.Navigator
import com.kobby.hymnal.presentation.screens.settings.PayWallScreen

// In your composable
val navigator = LocalNavigator.currentOrThrow

// Navigate to paywall
navigator.push(PayWallScreen())
```

### 2. Check Subscription Status

Check if the user has an active subscription:

```kotlin
import com.kobby.hymnal.core.iap.SubscriptionManager
import org.koin.compose.koinInject

// In your composable
val subscriptionManager: SubscriptionManager = koinInject()

LaunchedEffect(Unit) {
    subscriptionManager.isUserSubscribed { isSubscribed ->
        if (isSubscribed) {
            // User has active subscription
            // Enable premium features
        } else {
            // User doesn't have subscription
            // Show limited features or navigate to paywall
        }
    }
}
```

### 3. Conditional Feature Access

Show premium features only to subscribers:

```kotlin
@Composable
fun HomeScreen() {
    val subscriptionManager: SubscriptionManager = koinInject()
    var isPremium by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        subscriptionManager.isUserSubscribed { isSubscribed ->
            isPremium = isSubscribed
        }
    }
    
    Column {
        // Always available features
        BasicFeatures()
        
        // Premium features
        if (isPremium) {
            PremiumFeatures()
        } else {
            LockedFeatureCard(
                onClick = {
                    navigator.push(PayWallScreen())
                }
            )
        }
    }
}
```

### 4. Subscription Settings

Allow users to manage their subscription:

```kotlin
@Composable
fun SettingsScreen() {
    val subscriptionManager: SubscriptionManager = koinInject()
    var isPremium by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        subscriptionManager.isUserSubscribed { isSubscribed ->
            isPremium = isSubscribed
        }
    }
    
    Column {
        if (isPremium) {
            ListItem(
                title = "Manage Subscription",
                subtitle = "View and manage your subscription",
                onClick = {
                    // Opens App Store (iOS) or Play Store (Android)
                    subscriptionManager.manageSubscription()
                }
            )
        } else {
            ListItem(
                title = "Get Premium",
                subtitle = "Unlock all features",
                onClick = {
                    navigator.push(PayWallScreen())
                }
            )
        }
    }
}
```

## Advanced Usage

### Custom PayWall Trigger

Show paywall when user tries to access premium feature:

```kotlin
@Composable
fun PremiumFeatureButton() {
    val navigator = LocalNavigator.currentOrThrow
    val subscriptionManager: SubscriptionManager = koinInject()
    
    Button(
        onClick = {
            subscriptionManager.isUserSubscribed { isSubscribed ->
                if (isSubscribed) {
                    // Open premium feature
                    navigator.push(PremiumFeatureScreen())
                } else {
                    // Show paywall
                    navigator.push(PayWallScreen())
                }
            }
        }
    ) {
        Text("Premium Feature")
    }
}
```

### Subscription State Manager

Create a shared state holder for subscription status:

```kotlin
class SubscriptionStateHolder(
    private val subscriptionManager: SubscriptionManager
) {
    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()
    
    init {
        refreshSubscriptionStatus()
    }
    
    fun refreshSubscriptionStatus() {
        subscriptionManager.isUserSubscribed { isSubscribed ->
            _isPremium.value = isSubscribed
        }
    }
}

// Add to Koin module
val appModule = module {
    single { SubscriptionStateHolder(get()) }
}

// Use in composables
@Composable
fun MyScreen() {
    val subscriptionState: SubscriptionStateHolder = koinInject()
    val isPremium by subscriptionState.isPremium.collectAsState()
    
    // Use isPremium state
}
```

### OnBoarding with PayWall

Show paywall during onboarding:

```kotlin
@Composable
fun OnboardingFlow() {
    val navigator = LocalNavigator.currentOrThrow
    var currentStep by remember { mutableStateOf(0) }
    
    when (currentStep) {
        0 -> WelcomeScreen(onNext = { currentStep = 1 })
        1 -> FeaturesScreen(onNext = { currentStep = 2 })
        2 -> PayWallScreen() // Integrated into onboarding
        else -> {
            // Complete onboarding
            navigator.replaceAll(HomeScreen())
        }
    }
}
```

## Product IDs Reference

### iOS
- **Yearly Subscription**: `ios_yearly_subscription`
- **One-Time Purchase**: `ios_onetime_purchase`

### Android
- **Premium Subscription**: `premium_subscription`

## Error Handling

Handle purchase failures gracefully:

```kotlin
@Composable
fun PayWallWithErrorHandling() {
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    PayWallScreen()
    
    // The PayWallScreen already handles errors internally
    // But you can add additional error handling if needed
}
```

## Testing

### Mock Subscription Manager for Testing

```kotlin
class MockSubscriptionManager(
    private val mockIsSubscribed: Boolean = false
) : SubscriptionManager {
    override fun purchaseSubscription(plan: PayPlan, callback: (Boolean) -> Unit) {
        // Simulate purchase
        callback(true)
    }
    
    override fun isUserSubscribed(callback: (Boolean) -> Unit) {
        callback(mockIsSubscribed)
    }
    
    override fun manageSubscription() {
        // No-op for testing
    }
}

// Use in preview or tests
@Preview
@Composable
fun PreviewWithMockSubscription() {
    // Use mock in Koin for preview
}
```

## Best Practices

1. **Check subscription status on app launch** to ensure up-to-date state
2. **Cache subscription status** but refresh periodically
3. **Handle purchase errors gracefully** with user-friendly messages
4. **Show loading states** during purchase flow
5. **Disable purchase buttons** during processing to prevent double-purchases
6. **Log analytics events** for purchase funnel tracking
7. **Test with sandbox accounts** before production
8. **Implement restore purchases** for users who reinstall the app

## Troubleshooting

### iOS
- **Products not loading**: Check product IDs match App Store Connect
- **Purchase not completing**: Verify sandbox account is signed in
- **Subscription not persisting**: Check UserDefaults write permissions

### Android
- **Billing client not connecting**: Check Google Play Services is installed
- **Purchase not completing**: Verify test account has access to internal track
- **Acknowledgment failing**: Check network connection and retry logic

## Next Features to Implement

1. **Restore Purchases**: Allow users to restore previous purchases
2. **Receipt Validation**: Validate purchases server-side
3. **Subscription Expiry Handling**: Handle expired subscriptions gracefully
4. **Promo Codes**: Support promotional codes
5. **Free Trial**: Offer free trial period
6. **Analytics Integration**: Track purchase events
7. **A/B Testing**: Test different paywall designs

