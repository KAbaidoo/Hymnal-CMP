package com.kobby.hymnal.presentation.screens.settings

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.core.iap.SubscriptionManager
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class PayWallScreen : Screen {

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val subscriptionManager: SubscriptionManager = koinInject()
        val coroutineScope = rememberCoroutineScope()
        var isProcessing by remember { mutableStateOf(false) }
        var isRestoring by remember { mutableStateOf(false) }
        var purchaseError by remember { mutableStateOf<String?>(null) }
        var successMessage by remember { mutableStateOf<String?>(null) }

        val entitlementInfo by subscriptionManager.entitlementState.collectAsState()

        // Paywall should be non-dismissible when user needs the paywall (trial expired / subscription expired / none)
        val isDismissible = !entitlementInfo.needsPaywall

        PayWallContent(
            isLoading = isProcessing,
            isRestoring = isRestoring,
            errorMsg = purchaseError,
            successMsg = successMessage,
            trialDaysRemaining = entitlementInfo.trialDaysRemaining,
            isDismissible = isDismissible,
            onPurchase = { plan ->
                if (!isProcessing && !isRestoring) {
                    isProcessing = true
                    purchaseError = null
                    successMessage = null

                    // Handle purchase with the selected plan
                    subscriptionManager.purchaseSubscription(plan) { success ->
                        isProcessing = false
                        if (success) {
                            // Purchase successful, navigate back
                            navigator.pop()
                        } else {
                            // Handle purchase failure
                            purchaseError = "Purchase failed. Please try again."
                        }
                    }
                }
            },
            onRestore = {
                if (!isProcessing && !isRestoring) {
                    isRestoring = true
                    purchaseError = null
                    successMessage = null

                    subscriptionManager.restorePurchases { success ->
                        isRestoring = false
                        if (success) {
                            successMessage = "Purchases restored successfully!"
                            // Navigate back after a short delay
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(1500)
                                navigator.pop()
                            }
                        } else {
                            purchaseError = "No purchases found to restore."
                        }
                    }
                }
            },

            onCloseClick = {
                // Only allow closing the paywall when it is dismissible
                if (!isProcessing && !isRestoring && isDismissible) {
                    navigator.pop()
                }
            },
            onPrivacy = {
                // TODO: Navigate to privacy policy or open URL
            },
            onTerms = {
                // TODO: Navigate to terms of service or open URL
            }
        )
    }
}
