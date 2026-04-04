@file:Suppress("ALL")

package com.kobby.hymnal.presentation.screens.settings

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalUriHandler
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.core.iap.PurchaseManager
import com.kobby.hymnal.core.trace.TraceEvents
import com.kobby.hymnal.core.trace.TraceManager
import com.kobby.hymnal.core.trace.traceParams
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class PayWallScreen(
    private val fromGatedScreen: Boolean = false,
    private val entrySource: String = "unknown"
) : Screen {
    override val key = uniqueScreenKey

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val purchaseManager: PurchaseManager = koinInject()
        val traceManager: TraceManager = koinInject()
        val coroutineScope = rememberCoroutineScope()
        val uriHandler = LocalUriHandler.current
        var isProcessing by remember { mutableStateOf(false) }
        var isRestoring by remember { mutableStateOf(false) }
        var purchaseError by remember { mutableStateOf<String?>(null) }
        var successMessage by remember { mutableStateOf<String?>(null) }

        var planDetails by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

        LaunchedEffect(Unit) {
            traceManager.track(
                TraceEvents.SUPPORT_FUNNEL,
                traceParams("action" to "paywall_viewed", "entry_source" to entrySource)
            )
            purchaseManager.fetchPlanDetails { details ->
                planDetails = details.associate { it.id to it.formattedPrice }
            }
        }


        PayWallContent(
            isLoading = isProcessing,
            isRestoring = isRestoring,
            errorMsg = purchaseError,
            successMsg = successMessage,
            planPrices = planDetails,
            onPurchase = { plan ->
                if (!isProcessing && !isRestoring) {
                    isProcessing = true
                    purchaseError = null
                    successMessage = null
                    traceManager.track(
                        TraceEvents.SUPPORT_FUNNEL,
                        traceParams(
                            "action" to "purchase_attempted",
                            "entry_source" to entrySource,
                            "plan" to plan.name.lowercase()
                        )
                    )

                    // Handle purchase with the selected plan
                    purchaseManager.makePurchase(plan) { success ->
                        isProcessing = false
                        if (success) {
                            // Record donation to reset prompt counters
                            purchaseManager.usageTracker.recordDonationMade()
                            successMessage = "Thank you for your support!"
                            traceManager.track(
                                TraceEvents.SUPPORT_FUNNEL,
                                traceParams(
                                    "action" to "purchase_result",
                                    "entry_source" to entrySource,
                                    "plan" to plan.name.lowercase(),
                                    "result" to "success"
                                )
                            )

                            // Purchase successful, navigate back after short delay
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(1000)
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
                            // Handle purchase failure
                            purchaseError = "Purchase failed. Please try again."
                            traceManager.track(
                                TraceEvents.SUPPORT_FUNNEL,
                                traceParams(
                                    "action" to "purchase_result",
                                    "entry_source" to entrySource,
                                    "plan" to plan.name.lowercase(),
                                    "result" to "failed"
                                )
                            )
                        }
                    }
                }
            },
            onRestore = {
                if (!isProcessing && !isRestoring) {
                    isRestoring = true
                    purchaseError = null
                    successMessage = null
                    traceManager.track(
                        TraceEvents.SUPPORT_FUNNEL,
                        traceParams("action" to "restore_attempted", "entry_source" to entrySource)
                    )

                    purchaseManager.restorePurchases { success ->
                        isRestoring = false
                        if (success) {
                            successMessage = "Support restored — you won't see donation prompts anymore."
                            traceManager.track(
                                TraceEvents.SUPPORT_FUNNEL,
                                traceParams(
                                    "action" to "restore_result",
                                    "entry_source" to entrySource,
                                    "result" to "success"
                                )
                            )
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
                            purchaseError = "No previous purchase found!"
                            traceManager.track(
                                TraceEvents.SUPPORT_FUNNEL,
                                traceParams(
                                    "action" to "restore_result",
                                    "entry_source" to entrySource,
                                    "result" to "failed"
                                )
                            )
                        }
                    }
                }
            },
            onPlanSelected = { plan ->
                traceManager.track(
                    TraceEvents.SUPPORT_FUNNEL,
                    traceParams(
                        "action" to "plan_selected",
                        "entry_source" to entrySource,
                        "plan" to plan.name.lowercase()
                    )
                )
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
                // Open privacy policy in the user's browser
                uriHandler.openUri("https://mypockethymnal.com/privacy-policy")
            },
            onTerms = {
                // Open terms of service in the user's browser
                uriHandler.openUri("https://mypockethymnal.com/terms-of-service")
            }
        )
    }
}
