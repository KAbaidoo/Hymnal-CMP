package com.kobby.hymnal.core.iap

import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.presentation.screens.settings.PayWallScreen
import org.koin.compose.koinInject

/**
 * A composable that gates premium features.
 * 
 * If the user has access (trial or subscribed), shows the premium content.
 * If the user doesn't have access, shows the paywall or fallback content.
 * 
 * @param premiumContent The content to show when user has access
 * @param showPaywallOnDenied If true, navigates to paywall when access is denied. If false, shows fallbackContent
 * @param fallbackContent The content to show when user doesn't have access (only used if showPaywallOnDenied is false)
 */
@Composable
fun PremiumFeatureGate(
    premiumContent: @Composable () -> Unit,
    showPaywallOnDenied: Boolean = true,
    fallbackContent: @Composable (() -> Unit)? = null
) {
    val subscriptionManager: SubscriptionManager = koinInject()
    val navigator = LocalNavigator.currentOrThrow
    val entitlementInfo by subscriptionManager.entitlementState.collectAsState()
    
    LaunchedEffect(Unit) {
        // Initialize subscription manager to ensure we have latest state
        subscriptionManager.initialize()
    }
    
    if (entitlementInfo.hasAccess) {
        // User has access (trial or subscribed)
        premiumContent()
    } else {
        // User doesn't have access
        if (showPaywallOnDenied) {
            // Navigate to paywall
            LaunchedEffect(Unit) {
                navigator.push(PayWallScreen())
            }
        } else {
            // Show fallback content
            fallbackContent?.invoke()
        }
    }
}

/**
 * Simpler version that just checks access without gating.
 * Use this when you want to conditionally show UI elements based on subscription status.
 * 
 * @param onHasAccess Callback when user has access (trial or subscribed)
 * @param onNoAccess Callback when user doesn't have access
 */
@Composable
fun CheckPremiumAccess(
    onHasAccess: @Composable (EntitlementInfo) -> Unit,
    onNoAccess: @Composable (EntitlementInfo) -> Unit
) {
    val subscriptionManager: SubscriptionManager = koinInject()
    val entitlementInfo by subscriptionManager.entitlementState.collectAsState()
    
    LaunchedEffect(Unit) {
        subscriptionManager.initialize()
    }
    
    if (entitlementInfo.hasAccess) {
        onHasAccess(entitlementInfo)
    } else {
        onNoAccess(entitlementInfo)
    }
}
