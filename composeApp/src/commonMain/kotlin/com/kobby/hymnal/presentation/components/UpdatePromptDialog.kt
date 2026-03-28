package com.kobby.hymnal.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.kobby.hymnal.theme.HymnalAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UpdatePromptDialog(
    latestVersion: String,
    isMandatory: Boolean,
    onUpdateClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!isMandatory) {
                onDismissClick()
            }
        },
        title = {
            Text(
                text = "Update Available",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = if (isMandatory) {
                    "A critical update (v$latestVersion) is available. You must update the app to continue using it."
                } else {
                    "A new version (v$latestVersion) of the Anglican Hymnal is available. Update now to get the latest features and bug fixes!"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        confirmButton = {
            TextButton(onClick = onUpdateClick) {
                Text(
                    text = "Update Now",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        dismissButton = {
            if (!isMandatory) {
                TextButton(onClick = onDismissClick) {
                    Text(
                        text = "Later",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun UpdatePromptDialogDarkPreview(){
    HymnalAppTheme() {
        UpdatePromptDialog(
            latestVersion = "1.0.0",
            isMandatory = false,
            onUpdateClick = {},
            onDismissClick = {}
        )
    }
}
@Preview(showBackground = true)
@Composable
fun UpdatePromptDialogLightPreview(){
    HymnalAppTheme(darkTheme = true) {
        UpdatePromptDialog(
            latestVersion = "1.0.0",
            isMandatory = true,
            onUpdateClick = {},
            onDismissClick = {}
        )
    }
}
