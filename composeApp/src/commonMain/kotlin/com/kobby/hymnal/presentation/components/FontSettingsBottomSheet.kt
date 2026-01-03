package com.kobby.hymnal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Minimize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kobby.hymnal.presentation.screens.settings.PayWallContent
import com.kobby.hymnal.theme.HymnalAppTheme
import com.kobby.hymnal.theme.Shapes
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.arrow_left_s_line
import hymnal_cmp.composeapp.generated.resources.cd_back
import hymnal_cmp.composeapp.generated.resources.cd_get_started
import hymnal_cmp.composeapp.generated.resources.cd_home
import hymnal_cmp.composeapp.generated.resources.fonts_settings
import hymnal_cmp.composeapp.generated.resources.close
import hymnal_cmp.composeapp.generated.resources.font_decrease
import hymnal_cmp.composeapp.generated.resources.font_increase
import hymnal_cmp.composeapp.generated.resources.font
import hymnal_cmp.composeapp.generated.resources.home_3_line
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSettingsBottomSheet(
    onDismiss: () -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onFontChange: (String) -> Unit,
    currentFont: String = "Onest"
) {
    var selectedFont by remember { mutableStateOf(currentFont) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val fontOptions = listOf("Onest", "PlayFair Display")
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
                text = stringResource(Res.string.fonts_settings),
                style = MaterialTheme.typography.titleMedium,
                color = fontColor
            )

            IconButton(
                modifier = Modifier
                    .clip(shape)
                    .background(containerColor), 
                onClick = onDismiss 
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(Res.string.close),
                    tint = MaterialTheme.colorScheme.primary
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
                    containerColor = containerColor
                )
            ) {
                    Text(
                        text = "A-",
                        color = fontColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )

            }
            
            // Increase font size button
            Button(
                onClick = { onFontSizeChange(1f) },
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
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        }

        // Font label
        Text(
            text = stringResource(Res.string.font),
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = fontColor
        )

        // Font dropdown selector
        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedFont,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = containerColor, 
                    unfocusedIndicatorColor = Color.Transparent
                ), 
                shape = shape,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                fontOptions.forEach { font ->
                    DropdownMenuItem(
                        text = { Text(font) },
                        onClick = {
                            selectedFont = font
                            onFontChange(font)
                            isDropdownExpanded = false
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = fontColor
                        ),
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
    onFontChange: (String) -> Unit,
    currentFont: String = "Onest"
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            FontSettingsBottomSheet(
                onDismiss = onDismiss,
                onFontSizeChange = onFontSizeChange,
                onFontChange = onFontChange,
                currentFont = currentFont
            )
        }
    }
}

@Composable
@Preview
fun FontSettingsBottomSheetPreview() {
    HymnalAppTheme {
        FontSettingsBottomSheet(
            onDismiss = {},
            onFontSizeChange = {},
            onFontChange = {}
        )
    }
    // This function is just a placeholder to avoid errors in preview tools.
}