package com.kobby.hymnal.core.components

import android.content.res.Configuration
import androidx.compose.foundation.background
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSettingsBottomSheet(
    onDismiss: () -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onFontChange: (String) -> Unit
) {
    var selectedFont by remember { mutableStateOf("Onest") }
    val fontOptions = listOf("Onest", "Roboto", "Inter", "Open Sans", "Lato")
    val fontColor = MaterialTheme.colorScheme.onSurface
    val containerColor = MaterialTheme.colorScheme.surface
    val shape = Shapes.medium
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
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fonts & Settings",
                style = MaterialTheme.typography.titleMedium,
                color = fontColor
            )

            IconButton(modifier =  Modifier.clip(shape)
                .background(containerColor), onClick = onDismiss ) {
                Text(
                    text = "✕",
                    fontSize = 16.sp,
                    color = fontColor,
                    fontWeight = FontWeight.Normal
                )

            }
        }

        // Font size adjustment buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Decrease font size button
            Button(
                onClick = { onFontSizeChange(-1f) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor =containerColor
                )
            ) {
                Text(
                    text = "A-",
                    color =fontColor,
                    fontWeight = FontWeight.Medium
                )
            }
            // Increase font size button
            Button(
                onClick = { onFontSizeChange(-1f) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor
                )
            ) {
                Text(
                    text = "A+",
                    color = fontColor,
                    fontWeight = FontWeight.Medium
                )
            }



        }

        // Font label
        Text(
            text = "Font",
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = fontColor

        )

        // Font dropdown selector
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedFont,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                },
                colors = TextFieldDefaults.colors(unfocusedContainerColor = containerColor, unfocusedIndicatorColor = Color.Transparent), shape = shape,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            // Dropdown menu content would go here
            // (Not fully implemented for simplicity)
            ExposedDropdownMenu(
                expanded = false,
                onDismissRequest = { }
            ) {
                fontOptions.forEach { font ->
                    DropdownMenuItem(
                        text = { Text(font) },
                        onClick = {
                            selectedFont = font
                            onFontChange(font)
                        }
                    )
                }
            }
        }
    }
}


// For actual implementation with ModalBottomSheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSettingsModal(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onFontChange: (String) -> Unit
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color.White
        ) {
            FontSettingsBottomSheet(
                onDismiss = onDismiss,
                onFontSizeChange = onFontSizeChange,
                onFontChange = onFontChange
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FontSettingsBottomSheetPreview() {
    HymnalAppTheme {
            FontSettingsBottomSheet(
                onDismiss = { },
                onFontSizeChange = { },
                onFontChange = { }
            )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun FontSettingsBottomSheetLightPreview() {
    HymnalAppTheme {
        FontSettingsBottomSheet(
            onDismiss = { },
            onFontSizeChange = { },
            onFontChange = { }
        )
    }
}