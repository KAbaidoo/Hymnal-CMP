package com.kobby.hymnal.start

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.composeApp.database.Hymn
import com.kobby.hymnal.core.database.HymnRepository
import com.kobby.hymnal.presentation.components.ScreenBackground
import com.kobby.hymnal.presentation.screens.home.HomeScreen
import com.kobby.hymnal.presentation.screens.hymns.HymnDetailScreen
import com.kobby.hymnal.theme.HymnalAppTheme
import com.kobby.hymnal.theme.Shapes
import com.russhwolf.settings.Settings
import org.koin.compose.koinInject
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.book_open
import hymnal_cmp.composeapp.generated.resources.piano_hands
import hymnal_cmp.composeapp.generated.resources.anglican_hymnal_multiline
import hymnal_cmp.composeapp.generated.resources.cd_get_started
import hymnal_cmp.composeapp.generated.resources.created_by
import hymnal_cmp.composeapp.generated.resources.author_name
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val AUTO_NAVIGATION_DELAY_MS = 5000L

class StartScreen : Screen {

    private val settings = Settings()

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val repository: HymnRepository = koinInject()
        var randomHymn by remember { mutableStateOf<Hymn?>(null) }

        // Fetch random hymn when screen loads
        LaunchedEffect(Unit) {
            try {
                randomHymn = repository.getRandomHymn()
            } catch (e: Exception) {
                // Silently handle any database errors
                randomHymn = null
            }
        }

        LaunchedEffect(Unit) {
            delay(AUTO_NAVIGATION_DELAY_MS)
            navigator.push(HomeScreen())
        }

        StartScreenContent(
            randomHymn = randomHymn,
            onStartButtonClicked = {
                navigator.push(HomeScreen())
            },
            onRandomHymnClicked = { hymn ->
                navigator.push(HymnDetailScreen(hymn))
            }
        )
    }

}

@Composable
fun StartScreenContent(
    modifier: Modifier = Modifier,
    randomHymn: Hymn? = null,
    onStartButtonClicked: () -> Unit,
    onRandomHymnClicked: (Hymn) -> Unit = {}
) {
    // Animation states
    var isCardVisible by remember { mutableStateOf(false) }
    var isHymnNumberVisible by remember { mutableStateOf(false) }
    var isContentVisible by remember { mutableStateOf(false) }
    
    // Trigger animations when hymn data loads
    LaunchedEffect(randomHymn) {
        if (randomHymn != null) {
            delay(300) // Initial delay for smooth appearance
            isCardVisible = true
            delay(100) // Small delay for card to start appearing
            isHymnNumberVisible = true
            delay(200) // Stagger the content text
            isContentVisible = true
        } else {
            // Reset animation states if hymn is null
            isCardVisible = false
            isHymnNumberVisible = false
            isContentVisible = false
        }
    }
    
    // Animated slide values for dynamic entrance
    val cardOffsetY by animateDpAsState(
        targetValue = if (isCardVisible) 0.dp else 50.dp,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        )
    )
    
    val cardAlpha by animateFloatAsState(
        targetValue = if (isCardVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        )
    )
    
    val hymnNumberOffsetY by animateDpAsState(
        targetValue = if (isHymnNumberVisible) 0.dp else 20.dp,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        )
    )
    
    val hymnNumberAlpha by animateFloatAsState(
        targetValue = if (isHymnNumberVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        )
    )
    
    val contentOffsetY by animateDpAsState(
        targetValue = if (isContentVisible) 0.dp else 30.dp,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        )
    )
    
    val contentAlpha by animateFloatAsState(
        targetValue = if (isContentVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        )
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column {
            ScreenBackground(modifier = Modifier.weight(0.5f).fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        modifier = Modifier.size(80.dp),
                        painter = painterResource(Res.drawable.book_open),
                        contentDescription = null,
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = stringResource(Res.string.anglican_hymnal_multiline),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.weight(0.5f))
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(x = 28.dp, y = (-30).dp)
        ) {
            Box {
                Image(
                    modifier = Modifier
                        .height(400.dp)
                        .width(300.dp)
                        .clip(Shapes.large),
                    painter = painterResource(Res.drawable.piano_hands),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                // Random hymn excerpt overlay with animations
                randomHymn?.let { hymn ->
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .width(300.dp)
                            .offset(y = cardOffsetY)
                            .alpha(cardAlpha)
                            .clickable { onRandomHymnClicked(hymn) },
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.2f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp)
                                .padding(vertical = 16.dp)
                        ) {
                            Text(
                                modifier = Modifier
                                    .offset(y = hymnNumberOffsetY)
                                    .alpha(hymnNumberAlpha),
                                text = "Hymn ${hymn.number}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                modifier = Modifier
                                    .offset(y = contentOffsetY)
                                    .alpha(contentAlpha),
                                text = hymn.content.take(170) + if (hymn.content.length > 170) "..." else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f),
                                maxLines = 5,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                
                IconButton(
                    onClick = onStartButtonClicked,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(56.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = Shapes.medium
                        )
                        .clip(Shapes.medium)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowForward,
                        contentDescription = stringResource(Res.string.cd_get_started),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                modifier = Modifier.padding( vertical = 8.dp),
                text = stringResource(Res.string.created_by),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier.padding(0.dp),
                text = stringResource(Res.string.author_name),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

