package com.kobby.hymnal.ancient_modern

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kobby.hymnal.core.components.ListScreen
import com.kobby.hymnal.theme.HymnalAppTheme



@Composable
fun AMListScreenContent(){
    val items = List(20) { _ -> "A&M HYMN 1" }
    ListScreen(
    titleCollapsed = "Ancient & Modern",
    titleExpanded = "Ancient\n& Modern",
    items = items,
    searchText = "",
    onSearchTextChanged = {})
}


@Preview
@Composable
fun ContentPreview() {
    HymnalAppTheme {
        AMListScreenContent()
    }
}

