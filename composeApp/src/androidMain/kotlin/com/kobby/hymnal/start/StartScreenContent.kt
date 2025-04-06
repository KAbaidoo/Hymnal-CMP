package com.kobby.hymnal.start

import androidx.annotation.Dimension
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.UiMode
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.R
import com.kobby.hymnal.theme.HymnalAppTheme
import com.kobby.hymnal.theme.Shapes
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.book_leaf


@Composable
private fun StartScreenContent(
    modifier: Modifier = Modifier,
    onStartButtonClicked: () -> Unit
) {
Box (  modifier = Modifier
    .fillMaxSize()
    .systemBarsPadding()
    .background(MaterialTheme.colorScheme.surface)){
    Column {
        ScreenBackground(modifier = Modifier.weight(0.5f).fillMaxSize()) {
            Column( modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(80.dp),
                    painter = painterResource(R.drawable.book_open),
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "Anglican\nHymnal",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Spacer(
            modifier = Modifier
                .weight(0.5f)
        )
    }

    Column(modifier = Modifier
        .align(Alignment.BottomCenter)
        .offset(x = 30.dp, y = (-40).dp)) {
        Image(
            modifier = Modifier
                .height(390.dp)
                .width(260.dp)
                .clip(Shapes.large),
            painter = painterResource(R.drawable.piano_hands),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier.padding(16.dp),
            text = "created by\nDennis Abban",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

}


@Composable
fun ScreenBackground(modifier: Modifier, content: @Composable () -> Unit){
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(250.dp),
                painter = painterResource(R.drawable.book_leaf),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            content()
    }
}

@Preview
@Composable
fun StartScreenContentPreview() {
    HymnalAppTheme {
        StartScreenContent {

        }
    }

}
