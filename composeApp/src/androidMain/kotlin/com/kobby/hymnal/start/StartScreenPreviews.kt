package com.kobby.hymnal.start

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kobby.hymnal.theme.HymnalAppTheme
import com.kobby.hymnal.utils.DevicePreviews

@Preview(name = "Start Screen - Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Start Screen - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StartScreenContentPreview() {
    HymnalAppTheme {
        StartScreenContent(
            onStartButtonClicked = { /* Preview - no action */ }
        )
    }
}

@DevicePreviews
@Composable
fun StartScreenDevicePreview() {
    HymnalAppTheme {
        StartScreenContent(
            onStartButtonClicked = { /* Preview - no action */ }
        )
    }
}