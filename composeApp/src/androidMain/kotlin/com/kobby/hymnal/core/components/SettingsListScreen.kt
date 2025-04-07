package com.kobby.hymnal.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun SettingsListScreen(
    titleCollapsed:String,
    items: List<String>,
    action: @Composable () -> Unit = {}
){
    ContentScreen(
        titleCollapsed = titleCollapsed,
        titleExpanded = titleCollapsed,
        actionButtons = action,
        content = { innerPadding ->

            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
            ) {

                items(items) { item ->
                    ListItem(title = item)

                }
            }
        },
        bottomBar = {

        }
    )
}
