package com.kobby.hymnal.presentation.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange

/**
 * Custom TextToolbar that monitors text selection status to enable highlighting.
 * When text is selected, it captures the selection range and triggers highlight UI.
 */
class CustomTextToolbar(
    private val defaultToolbar: TextToolbar,
    private val clipboardManager: ClipboardManager,
    private val onSelectionChanged: (TextRange?) -> Unit,
    private val textContent: String
) : TextToolbar {
    
    private var isMenuVisible by mutableStateOf(false)
    
    override val status: TextToolbarStatus
        get() = if (isMenuVisible) TextToolbarStatus.Shown else TextToolbarStatus.Hidden

    override fun hide() {
        defaultToolbar.hide()
        isMenuVisible = false
        onSelectionChanged(null)
    }

    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?
    ) {
        isMenuVisible = true
        
        // Capture selected text to determine range
        val previousClipboard = clipboardManager.getText()
        
        // Temporarily copy to get selection
        onCopyRequested?.invoke()
        val copiedText = clipboardManager.getText()
        
        // Restore clipboard
        if (previousClipboard != null) {
            clipboardManager.setText(previousClipboard)
        } else {
            clipboardManager.setText(AnnotatedString(""))
        }
        
        // Process the selection
        copiedText?.let { annotatedString ->
            val selectedText = annotatedString.text
            if (selectedText.isNotEmpty()) {
                val startIndex = textContent.indexOf(selectedText)
                if (startIndex >= 0) {
                    val endIndex = startIndex + selectedText.length
                    val range = TextRange(startIndex, endIndex)
                    onSelectionChanged(range)
                }
            }
        }
        
        // Show the default toolbar
        defaultToolbar.showMenu(
            rect,
            onCopyRequested,
            onPasteRequested,
            onCutRequested,
            onSelectAllRequested
        )
    }
}