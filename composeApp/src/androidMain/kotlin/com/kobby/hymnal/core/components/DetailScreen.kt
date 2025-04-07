package com.kobby.hymnal.core.components

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
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.R

@Composable
fun DetailScreen(
    titleCollapsed:String,
    titleExpanded:String,
    onFavouriteClicked: ()-> Unit = {},
    onFontSettingsClicked: ()-> Unit = {},
    onShareClicked: ()-> Unit = {},
    contentText: String
){
    ContentScreen(
        titleCollapsed =  titleCollapsed,
        titleExpanded = titleExpanded,
        actionButtons = {
            Row {
                val iconSize = 20.dp
                val tintColor = MaterialTheme.colorScheme.secondary

                IconButton(
                    onClick = onFavouriteClicked) {
                    Icon(
                        modifier = Modifier.size(iconSize),
                        imageVector = ImageVector.vectorResource(R.drawable.heart_2_line),
                        contentDescription = "favorite",
                        tint = tintColor
                    )
                }
                IconButton(onClick = onFontSettingsClicked) {
                    Icon(
                        modifier = Modifier.size(iconSize),
                        imageVector = ImageVector.vectorResource(R.drawable.font_settng),
                        contentDescription = "font settings",
                        tint = tintColor
                    )
                }
                IconButton(onClick = onShareClicked) {
                    Icon(
                        modifier = Modifier.size(iconSize),
                        imageVector = ImageVector.vectorResource(R.drawable.share_line),
                        contentDescription = "share",
                        tint = tintColor
                    )
                }
            }
        },
        content = { innerPadding ->

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
            ) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = contentText, style = MaterialTheme.typography.bodyMedium
                    )
                }

            }
        },
        bottomBar ={}
    )
}