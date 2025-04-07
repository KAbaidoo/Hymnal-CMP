package com.kobby.hymnal.settings

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kobby.hymnal.core.components.ListScreen
import com.kobby.hymnal.core.components.SettingsListScreen
import com.kobby.hymnal.theme.HymnalAppTheme

@Composable
fun HistoryScreenContent(){
    val items = List(20) { _ -> "A&M HYMN 1" }
    SettingsListScreen(
        titleCollapsed = "History",
        items = items,
        action = {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Clear all", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            }

        })
}


@Preview
@Composable
fun HistoryScreenContentPreview() {
    HymnalAppTheme {
        HistoryScreenContent()
    }
}
