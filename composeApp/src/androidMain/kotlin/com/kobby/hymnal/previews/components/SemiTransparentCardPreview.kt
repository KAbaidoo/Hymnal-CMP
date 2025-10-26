package com.kobby.hymnal.previews.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.presentation.components.ScreenBackground
import com.kobby.hymnal.presentation.components.SemiTransparentCard
import com.kobby.hymnal.theme.HymnalAppTheme
import com.kobby.hymnal.utils.DevicePreviews

@Preview(name = "SemiTransparentCard - Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "SemiTransparentCard - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SemiTransparentCardPreview() {
    HymnalAppTheme {
        ScreenBackground {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                SemiTransparentCard {
                    Text(
                        text = "Find Your Hymns",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "This is a semi-transparent card that adapts to both light and dark themes using Material3 Card component.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Preview(name = "SemiTransparentCard - Standalone Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "SemiTransparentCard - Standalone Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SemiTransparentCardStandalonePreview() {
    HymnalAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            SemiTransparentCard {
                Text(
                    text = "Card Title",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Card content with semi-transparent background",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@DevicePreviews
@Composable
fun SemiTransparentCardDevicePreview() {
    HymnalAppTheme {
        ScreenBackground {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                SemiTransparentCard {
                    Text(
                        text = "Device Preview",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Testing on different device sizes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}