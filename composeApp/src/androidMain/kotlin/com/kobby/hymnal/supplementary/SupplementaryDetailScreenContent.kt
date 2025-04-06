package com.kobby.hymnal.supplementary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.R
import com.kobby.hymnal.components.DetailScreen
import com.kobby.hymnal.core.components.ContentScreen
import com.kobby.hymnal.theme.HymnalAppTheme


@Composable
fun SupplementaryDetailScreenContent(){
    DetailScreen(
        titleCollapsed = "Supplementary",
        titleExpanded = "Supplementary",
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
fun SupplementaryDetailScreenContentPreview() {
    HymnalAppTheme {
        SupplementaryDetailScreenContent()
    }
}
