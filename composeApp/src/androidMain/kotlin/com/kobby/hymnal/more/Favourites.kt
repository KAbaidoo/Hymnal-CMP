package com.kobby.hymnal.more

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kobby.hymnal.core.components.ListScreen
import com.kobby.hymnal.core.components.SettingsListScreen
import com.kobby.hymnal.theme.HymnalAppTheme

@Composable
fun FavouritesScreenContent(){
    val items = List(20) { _ -> "A&M HYMN 1" }
    SettingsListScreen(
        titleCollapsed = "Favourites",
        items = items,
        action = {
        })
}


@Preview
@Composable
fun FavouritesScreenContentPreview() {
    HymnalAppTheme {
        FavouritesScreenContent()
    }
}
