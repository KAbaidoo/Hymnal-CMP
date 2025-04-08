package com.kobby.hymnal.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.core.components.ContentScreen
import com.kobby.hymnal.core.components.ListItem
import com.kobby.hymnal.theme.HymnalAppTheme


@Composable
fun MoreScreenContent(){
    var checked by remember { mutableStateOf(true) }
    ContentScreen(
        titleCollapsed = "More",
        titleExpanded =  "More",
        actionButtons = null,
        content = { innerPadding ->
            val items = listOf("Bookmarks","History","Highlights")

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)) {

                items.forEach { item ->
                    ListItem(title = item)
                }

                Text(text = "Settings", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp))
                Card (elevation = CardDefaults.elevatedCardElevation(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)){

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dark mode",
                            style = MaterialTheme.typography.bodyMedium,
                        )

                        Switch(
                            modifier = Modifier
                                .padding(10.dp)
                                .height(8.dp),
                            checked = checked,
                            onCheckedChange = {
                                checked = it
                            }
                        )
                    }
                }

Spacer(modifier = Modifier.weight(1f))
                Text(text = "Version 1.0.1", style = MaterialTheme.typography.bodyMedium, modifier = Modifier
                    .padding(32.dp)
                    .align(Alignment.CenterHorizontally))
            }


        },
        bottomBar = {

        }
    )
}

@Preview
@Composable
fun MoreScreenContentPreview() {
    HymnalAppTheme {
        MoreScreenContent()
    }
}