package com.kobby.hymnal.core.components


import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kobby.hymnal.theme.HymnalAppTheme
import com.kobby.hymnal.theme.Shapes

@Composable
fun HighlightTextBottomSheet(
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    val fontColor = MaterialTheme.colorScheme.onSurface
    val shape = Shapes.medium

    // Highlight color options
    val colorOptions = listOf(
        Color(0xFFD6E8FF), // Light blue
        Color(0xFFE7DDFF), // Light purple
        Color(0xFFE3FFD6), // Light green
        Color(0xFFFFE8D6)  // Light peach
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header with title and close button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Highlight",
                style = MaterialTheme.typography.titleMedium,
                color = fontColor
            )

            IconButton(
                modifier = Modifier
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surface),
                onClick = onDismiss
            ) {
                Text(
                    text = "✕",
                    fontSize = 16.sp,
                    color = fontColor,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // Color selection row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            colorOptions.forEach { color ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clip(shape)
                        .background(color)
                        .clickable { onColorSelected(color) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighlightTextModal(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(),
            containerColor = MaterialTheme.colorScheme.background
        ) {
            HighlightTextBottomSheet(
                onDismiss = onDismiss,
                onColorSelected = onColorSelected
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HighlightTextBottomSheetPreview() {
    HymnalAppTheme {
        HighlightTextBottomSheet(
            onDismiss = { },
            onColorSelected = { }
        )
    }
}