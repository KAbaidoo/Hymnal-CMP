@file:Suppress("UNUSED_IMPORT", "unused")

package com.kobby.hymnal.presentation.screens.more.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.BuildKonfig
import com.kobby.hymnal.presentation.components.ContentScreen
import com.kobby.hymnal.presentation.components.ListItem
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.version
import hymnal_cmp.composeapp.generated.resources.more
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MoreScreenContent(
    onItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    showSupportItem: Boolean = true
) {
    ContentScreen(
        titleCollapsed = stringResource(Res.string.more),
        titleExpanded = stringResource(Res.string.more),
        actionButtons = null,
        content = { innerPadding ->
            val menuItems = mutableListOf<String>()
            if (showSupportItem) {
                menuItems.add("Support Development")
            }
            menuItems.addAll(listOf(
                "Favorites",
                "History",
                "Highlights"
            ))

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

                Spacer(modifier = Modifier.weight(1f))
                
                // Version info
                Text(
                    text = "${stringResource(Res.string.version)} ${BuildKonfig.VERSION_NAME}", 
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

@Preview(showBackground = true)
@Composable
fun MoreScreenContentPreview() {
    MoreScreenContent(
        onItemClick = {},
        onBackClick = {},
        onHomeClick = {}
    )
}