package com.kobby.hymnal.previews.more

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kobby.hymnal.composeApp.database.Hymn
import com.kobby.hymnal.presentation.screens.more.components.MoreScreenContent
import com.kobby.hymnal.presentation.screens.more.components.FavoritesContent
import com.kobby.hymnal.presentation.screens.more.components.HistoryContent
import com.kobby.hymnal.presentation.screens.more.components.HighlightsContent
import com.kobby.hymnal.theme.HymnalAppTheme

@Preview(name = "More Screen - Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "More Screen - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MoreScreenContentPreview() {
    HymnalAppTheme {
        MoreScreenContent(
            isDarkMode = false,
            onDarkModeToggle = {},
            onItemClick = {},
            onBackClick = {},
            onHomeClick = {}
        )
    }
}

@Preview(name = "Favorites - Empty")
@Composable
fun FavoritesEmptyPreview() {
    HymnalAppTheme {
        FavoritesContent(
            hymns = emptyList(),
            searchText = "",
            isLoading = false,
            error = null,
            onSearchTextChanged = {},
            onItemClick = {},
            onBackClick = {},
            onHomeClick = {}
        )
    }
}

@Preview(name = "Favorites - With Items")
@Composable
fun FavoritesWithItemsPreview() {
    HymnalAppTheme {
        FavoritesContent(
            hymns = sampleFavoriteHymns,
            searchText = "",
            isLoading = false,
            error = null,
            onSearchTextChanged = {},
            onItemClick = {},
            onBackClick = {},
            onHomeClick = {}
        )
    }
}

@Preview(name = "History - Empty")
@Composable
fun HistoryEmptyPreview() {
    HymnalAppTheme {
        HistoryContent(
            hymns = emptyList(),
            searchText = "",
            isLoading = false,
            error = null,
            onSearchTextChanged = {},
            onItemClick = {},
            onBackClick = {},
            onHomeClick = {},
            onClearHistory = {}
        )
    }
}

@Preview(name = "Highlights - Empty")
@Composable
fun HighlightsEmptyPreview() {
    HymnalAppTheme {
        HighlightsContent(
            isLoading = false,
            error = null,
            onBackClick = {},
            onHomeClick = {}
        )
    }
}

private val sampleFavoriteHymns = listOf(
    Hymn(
        id = 1,
        number = 23,
        title = "Praise, my soul, the King of heaven",
        category = "ancient_modern",
        content = "Praise, my soul, the King of heaven...",
        created_at = 0
    ),
    Hymn(
        id = 207,
        number = 207,
        title = "Our Blest Redeemer",
        category = "ancient_modern",
        content = "Our Blest Redeemer, ere He breathed...",
        created_at = 0
    )
)