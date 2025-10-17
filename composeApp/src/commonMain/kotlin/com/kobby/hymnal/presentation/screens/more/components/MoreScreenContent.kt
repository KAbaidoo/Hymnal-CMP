package com.kobby.hymnal.presentation.screens.more.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.presentation.components.ContentScreen
import com.kobby.hymnal.presentation.components.ListItem

@Composable
fun MoreScreenContent(
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    ContentScreen(
        titleCollapsed = "More",
        titleExpanded = "More",
        actionButtons = null,
        content = { innerPadding ->
            val menuItems = listOf("Bookmarks", "History", "Highlights")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Menu items
                menuItems.forEach { item ->
                    ListItem(
                        title = item,
                        onClick = { onItemClick(item) }
                    )
                }

                // Settings section
                Text(
                    text = "Settings", 
                    style = MaterialTheme.typography.bodyMedium, 
                    modifier = Modifier.padding(16.dp)
                )
                
                Card(
                    elevation = CardDefaults.elevatedCardElevation(), 
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ), 
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
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
                            checked = isDarkMode,
                            onCheckedChange = onDarkModeToggle
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                
                // Version info
                Text(
                    text = "Version 1.0.1", 
                    style = MaterialTheme.typography.bodyMedium, 
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(32.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        },
        onBackClick = onBackClick,
        onHomeClick = onHomeClick
    )
}