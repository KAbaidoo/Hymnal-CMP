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
import com.kobby.hymnal.core.settings.FontSettingsManager
import com.kobby.hymnal.core.sharing.ShareManager
import org.koin.compose.koinInject
import com.kobby.hymnal.presentation.components.DetailScreen
import com.kobby.hymnal.presentation.components.FontSettingsModal
import kotlinx.coroutines.launch

data class HymnDetailScreen(private val hymn: Hymn) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val repository: HymnRepository = koinInject()
        val shareManager: ShareManager = koinInject()
        var isFavorite by remember { mutableStateOf(false) }
        var showFontSettings by remember { mutableStateOf(false) }
        
        // Font settings
        val fontSettingsManager: FontSettingsManager = koinInject()
        val fontSettings by fontSettingsManager.fontSettings.collectAsState()
        
        // Check if hymn is favorite
        LaunchedEffect(Unit) {
            isFavorite = repository.isFavorite(hymn.id)
        }
        
        // Add to history when screen opens
        LaunchedEffect(hymn.id) {
            repository.addToHistory(hymn.id)
        }
        
        DetailScreen(
            hymn = hymn,
            isFavorite = isFavorite,
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
            onFavoriteClick = {
                scope.launch {
                    repository?.let { repo ->
                        if (isFavorite) {
                            repo.removeFromFavorites(hymn.id)
                        } else {
                            repo.addToFavorites(hymn.id)
                        }
                        isFavorite = !isFavorite
                    }
                }
            },
            onFontSettingsClick = {
                showFontSettings = true
            },
            onShareClick = {
                shareManager.shareHymn(hymn)
            },
            fontSettings = fontSettings
        )
        
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