package com.kobby.hymnal.presentation.screens.more

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.presentation.screens.more.components.MoreScreenContent
import com.kobby.hymnal.presentation.screens.settings.PayWallScreen
import com.kobby.hymnal.core.iap.PurchaseManager
import org.koin.compose.koinInject

class MoreScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val purchaseManager: PurchaseManager = koinInject()
        val entitlementInfo by purchaseManager.entitlementState.collectAsState()
        val showSupport = !entitlementInfo.hasSupported

        MoreScreenContent(
            onItemClick = { item ->
                when (item) {
                    "Favorites" -> navigator.push(FavoritesScreen())
                    "History" -> navigator.push(HistoryScreen())
                    "Highlights" -> navigator.push(HighlightsScreen())
                    "Support Development" -> navigator.push(PayWallScreen())
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