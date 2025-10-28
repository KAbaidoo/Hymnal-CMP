package com.kobby.hymnal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalClipboardManager
import com.kobby.hymnal.composeApp.database.Hymn
import com.kobby.hymnal.core.database.HymnRepository
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import com.kobby.hymnal.core.settings.FontSettings
import com.kobby.hymnal.theme.getAppFontFamily
import com.kobby.hymnal.theme.LightTextColor
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.font_settng
import hymnal_cmp.composeapp.generated.resources.heart_2_fill
import hymnal_cmp.composeapp.generated.resources.heart_2_line
import hymnal_cmp.composeapp.generated.resources.share_line
import hymnal_cmp.composeapp.generated.resources.cd_font_settings
import hymnal_cmp.composeapp.generated.resources.cd_share
import hymnal_cmp.composeapp.generated.resources.cd_add_favorite
import hymnal_cmp.composeapp.generated.resources.cd_remove_favorite
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.resources.stringResource

data class TextHighlight(
    val start: Int,
    val end: Int,
    val color: Color,
    val dbId: Long? = null // Database ID for persistence
)
private fun getCategoryAbbreviation(category: String?): String {
    return when (category) {
        "ancient_modern" -> "A&M"
        "supplementary" -> "Supp"
        "canticles" -> ""
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
    val repository: HymnRepository = koinInject()
    val coroutineScope = rememberCoroutineScope()
    
    var showHighlightBottomSheet by remember { mutableStateOf(false) }
    var selectedTextRange by remember { mutableStateOf<TextRange?>(null) }
    var currentHighlightColor by remember { mutableStateOf(Color(0xFFD6E8FF)) } // Default light blue
    var currentHighlightIndex by remember { mutableStateOf<Int?>(null) }
    val highlights = remember { mutableStateListOf<TextHighlight>() }
    val clipboardManager = LocalClipboardManager.current
    
    // Color palette for highlights (predefined colors)
    val highlightColors = listOf(
        Color(0xFFD6E8FF), // Light blue
        Color(0xFFE7DDFF), // Light purple
        Color(0xFFE3FFD6), // Light green
        Color(0xFFFFE8D6)  // Light peach
    )
    
    // Load existing highlights from database
    LaunchedEffect(hymn.id) {
        try {
            val dbHighlights = repository.getHighlightsForHymn(hymn.id)
            highlights.clear()
            dbHighlights.forEach { highlight ->
                // Use the stored color index to restore the original color
                val colorIndex = highlight.color_index.toInt().coerceIn(0, highlightColors.size - 1)
                val textHighlight = TextHighlight(
                    start = highlight.start_index.toInt(),
                    end = highlight.end_index.toInt(),
                    color = highlightColors[colorIndex],
                    dbId = highlight.id
                )
                highlights.add(textHighlight)
            }
        } catch (e: Exception) {
            // Handle error silently for now
        }
    }
    
    val customTextToolbar = remember {
        object : TextToolbar {
            override val status: TextToolbarStatus = TextToolbarStatus.Hidden
            
            override fun hide() {
                // Hide toolbar if needed
            }
            
            override fun showMenu(
                rect: Rect,
                onCopyRequested: (() -> Unit)?,
                onPasteRequested: (() -> Unit)?,
                onCutRequested: (() -> Unit)?,
                onSelectAllRequested: (() -> Unit)?
            ) {
                onCopyRequested?.invoke()
                val clipboardText = clipboardManager.getText()
                clipboardText?.let { annotatedString ->
                    val content = hymn.content ?: ""
                    val selectedText = annotatedString.text
                    val startIndex = content.indexOf(selectedText)
                    if (startIndex >= 0) {
                        val range = TextRange(startIndex, startIndex + selectedText.length)
                        selectedTextRange = range
                        
                        // Apply highlight immediately with current color
                        val colorIndex = highlightColors.indexOf(currentHighlightColor).coerceAtLeast(0)
                        val newHighlight = TextHighlight(range.start, range.end, currentHighlightColor)
                        highlights.add(newHighlight)
                        currentHighlightIndex = highlights.size - 1
                        
                        // Persist to database
                        coroutineScope.launch {
                            try {
                                repository.addHighlight(hymn.id, range.start.toLong(), range.end.toLong(), colorIndex.toLong())
                            } catch (e: Exception) {
                                // Handle error - could remove from memory if database save fails
                            }
                        }
                        
                        showHighlightBottomSheet = true
                    }
                }
            }
        }
    }
    
    fun buildHighlightedText(content: String): AnnotatedString {
        return buildAnnotatedString {
            append(content)
            highlights.forEachIndexed { index, highlight ->
                addStyle(
                    style = SpanStyle(
                        background = highlight.color,
                        color = LightTextColor
                    ),
                    start = highlight.start,
                    end = highlight.end
                )
                addStringAnnotation(
                    tag = "highlight",
                    annotation = index.toString(),
                    start = highlight.start,
                    end = highlight.end
                )
            }
        }
    }
    
    val hymnNumber = if (hymn.number == 0L) "Creed" else hymn.number.toString()
    ContentScreen(
        titleCollapsed = when {
            hymn.number == 0L -> "The Creed"
            hymn.category == "canticles" -> hymn.title ?: "Untitled"
            else -> "${getCategoryAbbreviation(hymn.category)} ${hymn.number}"
        },
        titleExpanded = when {
            hymn.category == "canticles" -> hymn.title ?: "Untitled"
            else -> "${getCategoryAbbreviation(hymn.category)}\n$hymnNumber"
        },
        actionButtons = if (showActionButtons) {
            {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = vectorResource(if (isFavorite) Res.drawable.heart_2_fill else Res.drawable.heart_2_line),
                            contentDescription = if (isFavorite) stringResource(Res.string.cd_remove_favorite) else stringResource(Res.string.cd_add_favorite),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    IconButton(onClick = onFontSettingsClick) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.font_settng),
                            contentDescription = stringResource(Res.string.cd_font_settings),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    IconButton(onClick = onShareClick) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.share_line),
                            contentDescription = stringResource(Res.string.cd_share),
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
                CompositionLocalProvider(LocalTextToolbar provides customTextToolbar) {
                    SelectionContainer {
                        ClickableText(
                            text = buildHighlightedText(hymn.content ?: "No content available"),
                            style = TextStyle(
                                fontFamily = getAppFontFamily(fontSettings.fontFamily),
                                fontWeight = FontWeight.Normal,
                                fontSize = fontSettings.fontSize.sp,
                                lineHeight = (fontSettings.fontSize * 1.8f).sp,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            onClick = { offset ->
                                val annotatedText = buildHighlightedText(hymn.content ?: "")
                                annotatedText.getStringAnnotations(
                                    tag = "highlight",
                                    start = offset,
                                    end = offset
                                ).firstOrNull()?.let { annotation ->
                                    val highlightIndex = annotation.item.toIntOrNull()
                                    highlightIndex?.let { index ->
                                        if (index < highlights.size) {
                                            currentHighlightIndex = index
                                            currentHighlightColor = highlights[index].color
                                            showHighlightBottomSheet = true
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
                
                HighlightTextModal(
                    isVisible = showHighlightBottomSheet,
                    onDismiss = { 
                        showHighlightBottomSheet = false
                        currentHighlightIndex = null
                        selectedTextRange = null
                    },
                    currentColor = currentHighlightColor,
                    onColorSelected = { color ->
                        currentHighlightColor = color
                        currentHighlightIndex?.let { index ->
                            highlights[index] = highlights[index].copy(color = color)
                        }
                        showHighlightBottomSheet = false
                        currentHighlightIndex = null
                        selectedTextRange = null
                    },
                    onRemoveHighlight = currentHighlightIndex?.let { index ->
                        {
                            val highlightToRemove = highlights[index]
                            highlights.removeAt(index)
                            
                            // Remove from database if it has a DB ID
                            highlightToRemove.dbId?.let { dbId ->
                                coroutineScope.launch {
                                    try {
                                        repository.removeHighlight(dbId)
                                    } catch (e: Exception) {
                                        // Handle error - could re-add to memory if database removal fails
                                    }
                                }
                            }
                            
                            showHighlightBottomSheet = false
                            currentHighlightIndex = null
                            selectedTextRange = null
                        }
                    }
                )
            }
        },
        onBackClick = onBackClick,
        onHomeClick = onHomeClick
    )
}