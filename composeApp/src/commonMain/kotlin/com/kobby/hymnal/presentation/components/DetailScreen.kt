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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import com.kobby.hymnal.composeApp.database.Hymn
import com.kobby.hymnal.core.settings.FontSettings
import com.kobby.hymnal.theme.getAppFontFamily
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.font_settng
import hymnal_cmp.composeapp.generated.resources.heart_2_line
import hymnal_cmp.composeapp.generated.resources.heart_2_fill
import hymnal_cmp.composeapp.generated.resources.share_line
import org.jetbrains.compose.resources.vectorResource

private fun getCategoryAbbreviation(category: String?): String {
    return when (category) {
        "ancient_modern" -> "A&M"
        "supplementary" -> "Supp"
        "creed" -> "The"
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
    onShareClick: () -> Unit = {},
    showActionButtons: Boolean = true,
    fontSettings: FontSettings = FontSettings()
) {
    val hymnNumber = if (hymn.number == 0L) "Creed" else hymn.number.toString()
    ContentScreen(
        titleCollapsed = if (hymn.number == 0L) {
            "The Creed"
        } else {
            "${getCategoryAbbreviation(hymn.category)} ${hymn.number}"
        },
        titleExpanded = "${getCategoryAbbreviation(hymn.category)}\n$hymnNumber",
        actionButtons = if (showActionButtons) {
            {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = vectorResource(if (isFavorite) Res.drawable.heart_2_fill else Res.drawable.heart_2_line),
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    IconButton(onClick = onFontSettingsClick) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.font_settng),
                            contentDescription = "Font settings",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    IconButton(onClick = onShareClick) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.share_line),
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        } else null,
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
                    style = TextStyle(
                        fontFamily = getAppFontFamily(fontSettings.fontFamily),
                        fontWeight = FontWeight.Normal,
                        fontSize = fontSettings.fontSize.sp,
                        lineHeight = (fontSettings.fontSize * 1.8f).sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        onBackClick = onBackClick,
        onHomeClick = onHomeClick
    )
}