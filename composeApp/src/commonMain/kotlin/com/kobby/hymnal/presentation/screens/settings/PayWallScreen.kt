@file:Suppress("ALL")

package com.kobby.hymnal.presentation.screens.settings

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.core.iap.PurchaseManager
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class PayWallScreen(
    private val fromGatedScreen: Boolean = false
) : Screen {

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val purchaseManager: PurchaseManager = koinInject()
        val coroutineScope = rememberCoroutineScope()
        var isProcessing by remember { mutableStateOf(false) }
        var isRestoring by remember { mutableStateOf(false) }
        var purchaseError by remember { mutableStateOf<String?>(null) }
        var successMessage by remember { mutableStateOf<String?>(null) }

        // Touch the vars in a no-op effect so static analysis recognizes they're read.
        LaunchedEffect(isProcessing, isRestoring, purchaseError, successMessage) {
            // no-op - variables are intentionally observed by the UI
        }

        // In freemium model, support sheet is always dismissible
        PayWallContent(
            isLoading = isProcessing,
            isRestoring = isRestoring,
            errorMsg = purchaseError,
            successMsg = successMessage,
            onPurchase = { plan ->
                if (!isProcessing && !isRestoring) {


                    // Handle purchase with the selected plan
                    purchaseManager.purchaseSubscription(plan) { success ->
                        if (success) {
                            // Record donation to reset prompt counters
                            purchaseManager.usageTracker.recordDonationMade()

                            // Purchase successful, navigate back
                            if (fromGatedScreen && navigator.canPop) {
                                // Pop both PayWall and the gated screen
                                navigator.pop()
                                if (navigator.canPop) {
                                    navigator.pop()
                                }
                            } else {
                                navigator.pop()
                            }
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

                    purchaseManager.restorePurchases { success ->
                        isRestoring = false
                        if (success) {
                            successMessage = "Support restored — you won't see donation prompts anymore."
                            // Navigate back after a short delay
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(1500)
                                if (fromGatedScreen && navigator.canPop) {
                                    // Pop both PayWall and the gated screen
                                    navigator.pop()
                                    if (navigator.canPop) {
                                        navigator.pop()
                                    }
                                } else {
                                    navigator.pop()
                                }
                            }
                        } else {
                            purchaseError = "No previous purchase found on this store account."
                        }
                    }
                }
            },

            onCloseClick = {
                if (!isProcessing) {
                    if (fromGatedScreen && navigator.canPop) {
                        // Pop both PayWall and the gated screen to avoid re-triggering
                        navigator.pop()
                        if (navigator.canPop) {
                            navigator.pop()
                        }
                    } else {
                        navigator.pop()
                    }
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
