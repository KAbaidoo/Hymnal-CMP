package com.kobby.hymnal.presentation.screens.more

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.core.trace.TraceEvents
import com.kobby.hymnal.core.trace.TraceManager
import com.kobby.hymnal.core.trace.traceParams
import com.kobby.hymnal.presentation.screens.more.components.MoreScreenContent
import com.kobby.hymnal.presentation.screens.settings.PayWallScreen
import com.kobby.hymnal.core.iap.PurchaseManager
import org.koin.compose.koinInject

class MoreScreen : Screen {
    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val purchaseManager: PurchaseManager = koinInject()
        val traceManager: TraceManager = koinInject()
        val entitlementInfo by purchaseManager.entitlementState.collectAsState()
        val showSupport = !entitlementInfo.hasSupported

        MoreScreenContent(
            onItemClick = { item ->
                traceManager.track(
                    TraceEvents.MORE_MENU_NAVIGATION,
                    traceParams("item" to item)
                )
                when (item) {
                    "Favorites" -> navigator.push(FavoritesScreen())
                    "History" -> navigator.push(HistoryScreen())
                    "Highlights" -> navigator.push(HighlightsScreen())
                    "Support Development" -> navigator.push(PayWallScreen(entrySource = "more_menu"))
                }
            },
            onBackClick = { navigator.pop() },
            onHomeClick = { 
                // Navigate to home by popping until we reach HomeScreen or we can't pop anymore
                while (navigator.canPop) {
                    navigator.pop()
                    // Check if current screen is HomeScreen by trying to find it in the stack
                    if (navigator.lastItem is com.kobby.hymnal.presentation.screens.home.HomeScreen) {
                        break
                    }
                }
            },
            showSupportItem = showSupport
        )
    }
}
