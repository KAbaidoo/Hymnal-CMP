package com.kobby.hymnal.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.main.MainScreen
import com.kobby.hymnal.theme.Shapes
import com.russhwolf.settings.Settings
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.book_leaf
import hymnal_cmp.composeapp.generated.resources.book_open
import hymnal_cmp.composeapp.generated.resources.piano_hands
import org.jetbrains.compose.resources.painterResource

class StartScreen : Screen {

    private val settings = Settings()

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).windowInsetsPadding(
            WindowInsets.safeDrawing), contentAlignment = Alignment.Center){
            Text(
                text = "Start screen",
                style = MaterialTheme.typography.titleLarge,
            )
        }
//        StartScreenContent(
//            onStartButtonClicked = {
//                settings.putBoolean("onboarding", false)
//
//                navigator.push(MainScreen())
//            }
//        )
    }

}
/*
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
                        painter = painterResource(Res.drawable.book_open),
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
                painter = painterResource(Res.drawable.piano_hands),
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


*/
