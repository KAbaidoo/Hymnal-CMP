package com.kobby.hymnal.ancient_modern

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kobby.hymnal.core.components.DetailScreen
import com.kobby.hymnal.theme.HymnalAppTheme


@Composable
fun AMDetailScreenContent(){

  DetailScreen(
        titleCollapsed = "A & M 207",
        titleExpanded = "A & M\n207",
        onFavouriteClicked =  {},
        onFontSettingsClicked = {},
        onShareClicked = {},
        contentText= "Worem ipsum dolor sit amet, consectetur adipiscing elit. Nunc vulputate libero et velit interdum, ac aliquet odio mattis. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Curabitur.\n" +
                "Worem ipsum dolor sit amet, consectetur adipiscing elit. Nunc vulputate libero et velit interdum, ac aliquet odio mattis. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos.\n" +
                "Worem ipsum dolor sit amet, consectetur adipiscing elit. Nunc vulputate libero et velit interdum, ac aliquet odio mattis. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos."
  )
}

@Preview
@Composable
fun AMDetailScreenContentPreview() {
    HymnalAppTheme {
        AMDetailScreenContent()
    }
}

