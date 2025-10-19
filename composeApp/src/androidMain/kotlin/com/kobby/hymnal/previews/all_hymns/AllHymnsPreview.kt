package com.kobby.hymnal.previews.all_hymns

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kobby.hymnal.composeApp.database.Hymn
import com.kobby.hymnal.presentation.screens.hymns.components.AllHymnsListContent
import com.kobby.hymnal.theme.HymnalAppTheme
import com.kobby.hymnal.utils.DevicePreviews

@Preview(name = "All Hymns - Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "All Hymns - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AllHymnsListContentPreview() {
    HymnalAppTheme {
        AllHymnsListContent(
            hymns = sampleAllHymns,
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

@Preview(name = "All Hymns - Loading")
@Composable
fun AllHymnsLoadingPreview() {
    HymnalAppTheme {
        AllHymnsListContent(
            hymns = emptyList(),
            searchText = "",
            isLoading = true,
            error = null,
            onSearchTextChanged = {},
            onItemClick = {},
            onBackClick = {},
            onHomeClick = {}
        )
    }
}

@Preview(name = "All Hymns - Error")
@Composable
fun AllHymnsErrorPreview() {
    HymnalAppTheme {
        AllHymnsListContent(
            hymns = emptyList(),
            searchText = "",
            isLoading = false,
            error = "Failed to load hymns from database",
            onSearchTextChanged = {},
            onItemClick = {},
            onBackClick = {},
            onHomeClick = {}
        )
    }
}

@Preview(name = "All Hymns - Search Results")
@Composable
fun AllHymnsSearchPreview() {
    HymnalAppTheme {
        AllHymnsListContent(
            hymns = sampleAllHymns.take(3),
            searchText = "Holy",
            isLoading = false,
            error = null,
            onSearchTextChanged = {},
            onItemClick = {},
            onBackClick = {},
            onHomeClick = {}
        )
    }
}

@DevicePreviews
@Composable
fun AllHymnsDevicePreview() {
    HymnalAppTheme {
        AllHymnsListContent(
            hymns = sampleAllHymns,
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

private val sampleAllHymns = listOf(
    Hymn(
        id = 1,
        number = 1,
        title = "All people that on earth do dwell",
        category = "ancient_modern",
        content = "All people that on earth do dwell,\nSing to the Lord with cheerful voice...",
        created_at = 0
    ),
    Hymn(
        id = 2,
        number = 2,
        title = "New every morning is the love",
        category = "ancient_modern",
        content = "New every morning is the love\nOur wakening and uprising prove...",
        created_at = 0
    ),
    Hymn(
        id = 780,
        number = 1,
        title = "Father, I dare believe",
        category = "supplementary",
        content = "Father, I dare believe\nThee merciful and true...",
        created_at = 0
    ),
    Hymn(
        id = 3,
        number = 3,
        title = "Awake, my soul, and with the sun",
        category = "ancient_modern",
        content = "Awake, my soul, and with the sun\nThy daily stage of duty run...",
        created_at = 0
    ),
    Hymn(
        id = 4,
        number = 4,
        title = "Holy, Holy, Holy! Lord God Almighty",
        category = "ancient_modern",
        content = "Holy, Holy, Holy! Lord God Almighty!\nEarly in the morning our song shall rise to Thee...",
        created_at = 0
    )
)