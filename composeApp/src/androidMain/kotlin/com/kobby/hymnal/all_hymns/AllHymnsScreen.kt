package com.kobby.hymnal.all_hymns

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kobby.hymnal.ancient_modern.AMDetailScreenContent
import com.kobby.hymnal.components.ListScreen
import com.kobby.hymnal.theme.HymnalAppTheme

@Composable
fun AllHymnsScreenContent(){
    val items = List(20) { _ -> "A&M HYMN 1" }
    ListScreen(
        titleCollapsed = "All Hymns",
        titleExpanded = "All Hymns",
        items = items,
        searchText = "",
        onSearchTextChanged = {})
}

@Preview
@Composable
fun AMDetailScreenContentPreview() {
    HymnalAppTheme {
        AllHymnsScreenContent()
    }
}