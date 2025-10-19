package com.kobby.hymnal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.TextUnit
import com.kobby.hymnal.composeApp.database.Hymn
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.font_settng
import hymnal_cmp.composeapp.generated.resources.heart_2_line
import hymnal_cmp.composeapp.generated.resources.share_line
import org.jetbrains.compose.resources.vectorResource

private fun getCategoryAbbreviation(category: String?): String {
    return when (category) {
        "ancient_modern" -> "A&M"
        "supplementary" -> "Supp"
        else -> "Hymn"
    }
}

@Composable
fun DetailScreen(
    hymn: Hymn,
    isFavorite: Boolean,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onFontSettingsClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
) {
    ContentScreen(
        titleCollapsed = "${getCategoryAbbreviation(hymn.category)} ${hymn.number}",
        titleExpanded = "${getCategoryAbbreviation(hymn.category)}\n${hymn.number}",
        actionButtons = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.heart_2_line),
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(onClick = onFontSettingsClick) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.font_settng),
                        contentDescription = "Font settings"
                    )
                }
                IconButton(onClick = onShareClick) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.share_line),
                        contentDescription = "Share"
                    )
                }
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = hymn.content ?: "No content available",
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = if (MaterialTheme.typography.bodyLarge.lineHeight == TextUnit.Unspecified) 1.8.em else MaterialTheme.typography.bodyLarge.lineHeight * 1.5f,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        onBackClick = onBackClick,
        onHomeClick = onHomeClick
    )
}