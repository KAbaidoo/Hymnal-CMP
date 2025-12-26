package com.kobby.hymnal.presentation.screens.settings

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.core.iap.SubscriptionManager
import org.koin.compose.koinInject

class PayWallScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val subscriptionManager: SubscriptionManager = koinInject()
        var isProcessing by remember { mutableStateOf(false) }
        var purchaseError by remember { mutableStateOf<String?>(null) }

        PayWallContent(
            isLoading = isProcessing,
            errorMsg = purchaseError,
            onPurchase = { plan ->
                if (!isProcessing) {
                    isProcessing = true
                    purchaseError = null

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
            onBackClick = {
                if (!isProcessing) {
                    navigator.pop()
                }
            },
            onHomeClick = {
                if (!isProcessing) {
                    // Pop all screens to go back to home
                    navigator.popAll()
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

