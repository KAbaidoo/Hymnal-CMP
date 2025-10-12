package com.kobby.hymnal.start

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kobby.hymnal.theme.HymnalAppTheme

@Preview
@Composable
fun StartScreenContentPreview() {
    HymnalAppTheme {
        StartScreenContent(
            onStartButtonClicked = { /* Preview - no action */ }
        )
    }
}