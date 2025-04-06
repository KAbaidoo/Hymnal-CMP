package com.kobby.hymnal.supplementary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.components.ListItem
import com.kobby.hymnal.components.ListScreen
import com.kobby.hymnal.core.components.ContentScreen
import com.kobby.hymnal.core.components.SearchTextField
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

