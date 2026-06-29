package com.kobby.hymnal.presentation.screens.more

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.core.iap.PurchaseManager
import com.kobby.hymnal.core.notifications.NotificationManager
import com.kobby.hymnal.core.notifications.NotificationPreferences
import com.kobby.hymnal.core.notifications.NotificationScheduler
import com.kobby.hymnal.core.trace.TraceEvents
import com.kobby.hymnal.core.trace.TraceManager
import com.kobby.hymnal.core.trace.traceParams
import com.kobby.hymnal.presentation.screens.more.components.MoreScreenContent
import com.kobby.hymnal.presentation.screens.settings.PayWallScreen
import org.koin.compose.koinInject

class MoreScreen : Screen {
    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val purchaseManager: PurchaseManager = koinInject()
        val notificationManager: NotificationManager = koinInject()
        val notificationPreferences: NotificationPreferences = koinInject()
        val notificationScheduler: NotificationScheduler = koinInject()

        val traceManager: TraceManager = koinInject()
        val entitlementInfo by purchaseManager.entitlementState.collectAsState()
        val showSupport = !entitlementInfo.hasSupported
        val notificationSettings by notificationPreferences.notificationSettings.collectAsState()

        MoreScreenContent(
            notificationsEnabled = notificationSettings.enabled,
            onNotificationsToggle = { enabled ->
                notificationPreferences.setEnabled(enabled)
                traceManager.track(
                    TraceEvents.NOTIFICATION_OPT_CHANGED,
                    traceParams("category" to "all", "enabled" to enabled)
                )
                if (enabled) {
                    notificationManager.requestPermission()
                }
                notificationScheduler.syncSchedules()
            },
            onTestNotification = {
                notificationManager.sendTestNotification()
            },
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
                while (navigator.canPop) {
                    navigator.pop()
                    if (navigator.lastItem is com.kobby.hymnal.presentation.screens.home.HomeScreen) {
                        break
                    }
                }
            },
            showSupportItem = showSupport
        )
    }
}
