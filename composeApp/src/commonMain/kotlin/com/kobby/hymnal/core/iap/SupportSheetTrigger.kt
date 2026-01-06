package com.kobby.hymnal.core.iap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.presentation.screens.settings.PayWallScreen
import org.koin.compose.koinInject

/**
 * A composable that shows the support sheet when a premium feature is accessed
 * without having supported the app.
 *
 * @param feature The premium feature being accessed
 * @param onAccessGranted Callback when user has access to the feature
 */
@Composable
fun PremiumFeatureAccess(
    feature: PremiumFeature,
    onAccessGranted: @Composable () -> Unit
) {
    val purchaseManager: PurchaseManager = koinInject()
    val navigator = LocalNavigator.currentOrThrow
    val entitlementInfo by purchaseManager.entitlementState.collectAsState()

    if (entitlementInfo.canAccessFeature(feature)) {
        // User has access, show the feature
        onAccessGranted()
    } else {
        // User doesn't have access, track the attempt and show support sheet
        LaunchedEffect(feature) {
            purchaseManager.usageTracker.recordFeatureAccessAttempt(feature)
            // Pass fromGatedScreen = true so PayWall knows to pop both screens on close
            navigator.push(PayWallScreen(fromGatedScreen = true))
        }
    }
}

/**
 * Check if user can access a premium feature without navigating.
 * Use this for conditional UI rendering.
 */
@Composable
fun canAccessPremiumFeature(feature: PremiumFeature): Boolean {
    val purchaseManager: PurchaseManager = koinInject()
    val entitlementInfo by purchaseManager.entitlementState.collectAsState()
    return entitlementInfo.canAccessFeature(feature)
}
