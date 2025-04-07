package com.kobby.hymnal.supplementary

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kobby.hymnal.core.components.ListScreen
import com.kobby.hymnal.theme.HymnalAppTheme


@Composable
fun SupplementaryListScreenContent(){
    val items = List(20) { _ -> "A&M HYMN 1" }
    ListScreen(
        titleCollapsed = "Supplementary",
        titleExpanded = "Supplementary",
        items = items,
        searchText = "",
        onSearchTextChanged = {})
}

@Preview
@Composable
fun SupplementaryListScreenContentPreview() {
    HymnalAppTheme {
        SupplementaryListScreenContent()
    }
}

