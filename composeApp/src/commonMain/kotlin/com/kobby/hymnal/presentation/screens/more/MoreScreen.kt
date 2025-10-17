package com.kobby.hymnal.presentation.screens.more

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.presentation.screens.more.components.MoreScreenContent

class MoreScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var isDarkMode by remember { mutableStateOf(false) }
        
        MoreScreenContent(
            isDarkMode = isDarkMode,
            onDarkModeToggle = { isDarkMode = it },
            onItemClick = { item ->
                when (item) {
                    "Bookmarks" -> navigator.push(FavoritesScreen())
                    "History" -> navigator.push(HistoryScreen())
                    "Highlights" -> navigator.push(HighlightsScreen())
                }
            },
            onBackClick = { navigator.pop() },
            onHomeClick = { 
                // Navigate to home by popping all screens
                while (navigator.canPop) {
                    navigator.pop()
                }
            }
        )
    }
}