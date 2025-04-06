package com.kobby.hymnal.ancient_modern

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kobby.hymnal.components.ListScreen
import com.kobby.hymnal.core.components.ContentScreen
import com.kobby.hymnal.core.components.SearchTextField
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

