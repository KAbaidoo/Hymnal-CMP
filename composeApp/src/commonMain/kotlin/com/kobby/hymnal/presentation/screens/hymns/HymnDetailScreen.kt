package com.kobby.hymnal.presentation.screens.hymns

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.composeApp.database.Hymn
import com.kobby.hymnal.core.database.HymnRepository
import com.kobby.hymnal.core.performance.PerformanceManager
import com.kobby.hymnal.core.settings.FontSettingsManager
import com.kobby.hymnal.core.sharing.ShareManager
import org.koin.compose.koinInject
import com.kobby.hymnal.presentation.components.DetailScreen
import com.kobby.hymnal.presentation.components.FontSettingsModal
import com.kobby.hymnal.presentation.screens.home.HomeScreen
import kotlinx.coroutines.launch

// Accept only primitive/serializable arguments to keep Screen serializable
data class HymnDetailScreen(
    private val hymnId: Long,
    private val fromStartScreen: Boolean = false
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val repository: HymnRepository = koinInject()
        val shareManager: ShareManager = koinInject()
        val performanceManager: PerformanceManager? = try { koinInject() } catch (e: Exception) { null }
        var isFavorite by remember { mutableStateOf(false) }
        var showFontSettings by remember { mutableStateOf(false) }
        var hymn by remember { mutableStateOf<Hymn?>(null) }

        // Font settings
        val fontSettingsManager: FontSettingsManager = koinInject()
        val fontSettings by fontSettingsManager.fontSettings.collectAsState()
        
        // Track screen render
        LaunchedEffect(Unit) {
            val screenTrace = performanceManager?.startTrace("screen_hymn_detail_render")
            screenTrace?.putAttribute("screen_name", "HymnDetailScreen")
            screenTrace?.putAttribute("hymn_id", hymnId.toString())
            // Stop trace after a short delay to measure initial render
            delay(100)
            screenTrace?.stop()
        }
        
        // Load hymn and favorite state
        LaunchedEffect(hymnId) {
            val loadTrace = performanceManager?.startTrace("hymn_detail_load_data")
            try {
                hymn = repository.getHymnById(hymnId)
                isFavorite = repository.isFavorite(hymnId)
                // Add to history once we navigate here
                repository.addToHistory(hymnId)
                loadTrace?.putAttribute("load_status", "success")
            } catch (e: Exception) {
                loadTrace?.putAttribute("load_status", "error")
                loadTrace?.putAttribute("error", e.message ?: "unknown")
            } finally {
                loadTrace?.stop()
            }
        }
        
        hymn?.let { loadedHymn ->
            DetailScreen(
                hymn = loadedHymn,
                isFavorite = isFavorite,
                onBackClick = {
                    if (fromStartScreen) {
                        // Navigate to home when coming from start screen
                        navigator.push(HomeScreen())
                    } else {
                        // Normal back navigation for other cases
                        navigator.pop()
                    }
                },
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
                onFavoriteClick = {
                    scope.launch {
                        repository.let { repo ->
                            if (isFavorite) {
                                repo.removeFromFavorites(hymnId)
                            } else {
                                repo.addToFavorites(hymnId)
                            }
                            isFavorite = !isFavorite
                        }
                    }
                },
                onFontSettingsClick = {
                    showFontSettings = true
                },
                onShareClick = {
                    shareManager.shareHymn(loadedHymn)
                },
                fontSettings = fontSettings
            )
        }

        // Font Settings Modal
        FontSettingsModal(
            isVisible = showFontSettings,
            onDismiss = { showFontSettings = false },
            onFontSizeChange = { sizeChange ->
                fontSettingsManager.updateFontSize(sizeChange)
            },
            onFontChange = { fontFamily ->
                fontSettingsManager.updateFontFamily(fontFamily)
            },
            currentFont = fontSettings.fontFamily
        )
    }
}