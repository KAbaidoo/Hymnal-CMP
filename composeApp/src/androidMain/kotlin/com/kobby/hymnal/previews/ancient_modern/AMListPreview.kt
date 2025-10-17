package com.kobby.hymnal.previews.ancient_modern

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kobby.hymnal.composeApp.database.Hymn
import com.kobby.hymnal.presentation.screens.hymns.components.AncientModernListContent
import com.kobby.hymnal.theme.HymnalAppTheme
import com.kobby.hymnal.utils.DevicePreviews

@Preview(name = "AM List - Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "AM List - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AMListContentPreview() {
    HymnalAppTheme {
        AncientModernListContent(
            hymns = sampleHymns,
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

@Preview(name = "AM List - Loading")
@Composable
fun AMListLoadingPreview() {
    HymnalAppTheme {
        AncientModernListContent(
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

@Preview(name = "AM List - Error")
@Composable
fun AMListErrorPreview() {
    HymnalAppTheme {
        AncientModernListContent(
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

@Preview(name = "AM List - Search Results")
@Composable
fun AMListSearchPreview() {
    HymnalAppTheme {
        AncientModernListContent(
            hymns = sampleHymns.take(3),
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
fun AMListDevicePreview() {
    HymnalAppTheme {
        AncientModernListContent(
            hymns = sampleHymns,
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

private val sampleHymns = listOf(
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
    ),
    Hymn(
        id = 5,
        number = 5,
        title = "Glory to Thee, my God, this night",
        category = "ancient_modern",
        content = "Glory to Thee, my God, this night,\nFor all the blessings of the light...",
        created_at = 0
    )
)