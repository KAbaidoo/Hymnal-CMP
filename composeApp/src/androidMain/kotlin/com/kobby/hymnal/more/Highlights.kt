package com.kobby.hymnal.more

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kobby.hymnal.core.components.SettingsListScreen
import com.kobby.hymnal.theme.HymnalAppTheme


@Composable
fun HighlightsScreenContent(){
    val items = List(20) { _ -> "A&M HYMN 1" }
    SettingsListScreen(
        titleCollapsed = "Highlights",
        items = items,
        action = {
        })
}


@Preview
@Composable
fun HighlightsScreenContentPreview() {
    HymnalAppTheme {
        HighlightsScreenContent()
    }
}
