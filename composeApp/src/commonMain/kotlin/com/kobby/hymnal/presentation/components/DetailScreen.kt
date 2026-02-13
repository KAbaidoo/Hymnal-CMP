package com.kobby.hymnal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.geometry.Offset
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
        "psalms" -> "Psalm"
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
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var lastSelectionDownPosition by remember { mutableStateOf<Offset?>(null) }
    var lastSelectionUpPosition by remember { mutableStateOf<Offset?>(null) }
    
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
    
    ContentScreen(
        titleCollapsed = when {
            hymn.category == "canticles" -> hymn.title ?: "Untitled"
            else -> "${getCategoryAbbreviation(hymn.category)} ${hymn.number}"
        },
        titleExpanded = when {
            // for canticles, take first word of title and append with line break and the rest of the title
            hymn.category == "canticles" -> {
                val title = hymn.title ?: "Untitled"
                val firstSpaceIndex = title.indexOf(' ')
                if (firstSpaceIndex > 0) {
                    val firstWord = title.substring(0, firstSpaceIndex)
                    val restOfTitle = title.substring(firstSpaceIndex + 1)
                    "$firstWord\n$restOfTitle"
                } else {
                    title
                }
            }
            else -> "${getCategoryAbbreviation(hymn.category)}\n${hymn.number}"
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
                SelectionContainer {
                    Text(
                        text = buildHighlightedText(hymn.content ?: "No content available"),
                        style = TextStyle(
                            fontFamily = getAppFontFamily(fontSettings.fontFamily),
                            fontWeight = FontWeight.Normal,
                            fontSize = fontSettings.fontSize.sp,
                            lineHeight = (fontSettings.fontSize * 1.8f).sp,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        onTextLayout = { textLayoutResult = it },
                        modifier = Modifier
                            .pointerInput(highlights.size, hymn.content) {
                                detectTapGestures(
                                    onTap = { position ->
                                        val layout = textLayoutResult ?: return@detectTapGestures
                                        val offset = layout.getOffsetForPosition(position)
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
                            .pointerInput(hymn.content) {
                                awaitPointerEventScope {
                                    while (true) {
                                        val downEvent = awaitPointerEvent(PointerEventPass.Final)
                                        val down = downEvent.changes.firstOrNull { it.pressed } ?: continue
                                        lastSelectionDownPosition = down.position
                                        val downTime = down.uptimeMillis
                                        var lastPosition = down.position
                                        var upTime = downTime
                                        var isUp = false
                                        while (!isUp) {
                                            val event = awaitPointerEvent(PointerEventPass.Final)
                                            val change = event.changes.firstOrNull { it.id == down.id }
                                            if (change == null || !change.pressed) {
                                                isUp = true
                                                lastSelectionUpPosition = lastPosition
                                                upTime = change?.uptimeMillis ?: upTime
                                            } else {
                                                lastPosition = change.position
                                                upTime = change.uptimeMillis
                                            }
                                        }

                                        val layout = textLayoutResult ?: continue
                                        val startPos = lastSelectionDownPosition ?: continue
                                        val endPos = lastSelectionUpPosition ?: continue
                                        val pressDuration = upTime - downTime
                                        if (pressDuration < viewConfiguration.longPressTimeoutMillis) {
                                            continue
                                        }
                                        val startOffset = layout.getOffsetForPosition(startPos)
                                        val endOffset = layout.getOffsetForPosition(endPos)

                                        val start: Int
                                        val end: Int
                                        if (startOffset <= endOffset) {
                                            val startWord = layout.getWordBoundary(startOffset)
                                            val endWord = layout.getWordBoundary(endOffset)
                                            if (startWord.length == 0 || endWord.length == 0) continue
                                            start = startWord.start
                                            end = endWord.end
                                        } else {
                                            val startWord = layout.getWordBoundary(endOffset)
                                            val endWord = layout.getWordBoundary(startOffset)
                                            if (startWord.length == 0 || endWord.length == 0) continue
                                            start = startWord.start
                                            end = endWord.end
                                        }

                                        selectedTextRange = TextRange(start, end)
                                        currentHighlightIndex = null
                                        currentHighlightColor = Color.Transparent
                                        showHighlightBottomSheet = true
                                    }
                                }
                            }
                    )
                }
                
                HighlightTextModal(
                    isVisible = showHighlightBottomSheet,
                    onDismiss = { 
                        showHighlightBottomSheet = false
                        currentHighlightIndex = null
                        selectedTextRange = null
                    },
                    currentColor = if (currentHighlightColor == Color.Transparent) null else currentHighlightColor,
                    onColorSelected = { color ->
                        currentHighlightColor = color
                        
                        currentHighlightIndex?.let { index ->
                            // Editing existing highlight - update color and persist to database
                            val highlightToUpdate = highlights[index]
                            highlights[index] = highlightToUpdate.copy(color = color)
                            
                            // Update color in database
                            highlightToUpdate.dbId?.let { dbId ->
                                val colorIndex = highlightColors.indexOf(color).coerceAtLeast(0)
                                coroutineScope.launch {
                                    try {
                                        repository.updateHighlightColor(dbId, colorIndex.toLong())
                                    } catch (e: Exception) {
                                        // Handle error - could revert memory change
                                    }
                                }
                            }
                        } ?: run {
                            // Creating new highlight from selected text
                            selectedTextRange?.let { range ->
                                val colorIndex = highlightColors.indexOf(color).coerceAtLeast(0)
                                val newHighlight = TextHighlight(range.start, range.end, color)
                                highlights.add(newHighlight)
                                
                                // Persist to database
                                coroutineScope.launch {
                                    try {
                                        repository.addHighlight(hymn.id, range.start.toLong(), range.end.toLong(), colorIndex.toLong())
                                    } catch (e: Exception) {
                                        // Handle error - could remove from memory if database save fails
                                        highlights.removeLastOrNull()
                                    }
                                }
                            }
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
