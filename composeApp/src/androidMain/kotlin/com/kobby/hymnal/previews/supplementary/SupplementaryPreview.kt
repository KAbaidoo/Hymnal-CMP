package com.kobby.hymnal.previews.supplementary

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kobby.hymnal.composeApp.database.Hymn
import com.kobby.hymnal.presentation.screens.hymns.components.SupplementaryListContent
import com.kobby.hymnal.theme.HymnalAppTheme
import com.kobby.hymnal.utils.DevicePreviews

@Preview(name = "Supplementary - Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Supplementary - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SupplementaryListContentPreview() {
    HymnalAppTheme {
        SupplementaryListContent(
            hymns = sampleSupplementaryHymns,
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

@Preview(name = "Supplementary - Loading")
@Composable
fun SupplementaryLoadingPreview() {
    HymnalAppTheme {
        SupplementaryListContent(
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

@Preview(name = "Supplementary - Error")
@Composable
fun SupplementaryErrorPreview() {
    HymnalAppTheme {
        SupplementaryListContent(
            hymns = emptyList(),
            searchText = "",
            isLoading = false,
            error = "Failed to load supplementary hymns",
            onSearchTextChanged = {},
            onItemClick = {},
            onBackClick = {},
            onHomeClick = {}
        )
    }
}

@Preview(name = "Supplementary - Search Results")
@Composable
fun SupplementarySearchPreview() {
    HymnalAppTheme {
        SupplementaryListContent(
            hymns = sampleSupplementaryHymns.take(2),
            searchText = "Father",
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
fun SupplementaryDevicePreview() {
    HymnalAppTheme {
        SupplementaryListContent(
            hymns = sampleSupplementaryHymns,
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

private val sampleSupplementaryHymns = listOf(
    Hymn(
        id = 780,
        number = 1,
        title = "Father, I dare believe",
        category = "supplementary",
        content = "Father, I dare believe\nThee merciful and true...",
        created_at = 0
    ),
    Hymn(
        id = 781,
        number = 2,
        title = "Deck thyself, my soul, with gladness",
        category = "supplementary",
        content = "Deck thyself, my soul, with gladness,\nLeave the gloomy haunts of sadness...",
        created_at = 0
    ),
    Hymn(
        id = 782,
        number = 3,
        title = "Come, O thou Traveller unknown",
        category = "supplementary",
        content = "Come, O thou Traveller unknown,\nWhom still I hold, but cannot see...",
        created_at = 0
    ),
    Hymn(
        id = 783,
        number = 4,
        title = "My God, I love thee",
        category = "supplementary",
        content = "My God, I love thee; not because\nI hope for heaven thereby...",
        created_at = 0
    ),
    Hymn(
        id = 784,
        number = 5,
        title = "Be still, my soul",
        category = "supplementary",
        content = "Be still, my soul: the Lord is on thy side;\nBear patiently the cross of grief or pain...",
        created_at = 0
    )
)