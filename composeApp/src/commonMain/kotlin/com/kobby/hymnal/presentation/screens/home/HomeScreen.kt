package com.kobby.hymnal.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalUriHandler
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.presentation.components.CategoryButtons
import com.kobby.hymnal.presentation.components.ScreenBackground
import com.kobby.hymnal.presentation.components.SemiTransparentCard
import com.kobby.hymnal.presentation.screens.hymns.HymnListScreen
import com.kobby.hymnal.core.database.HymnRepository
import com.kobby.hymnal.presentation.screens.more.FavoritesScreen
import com.kobby.hymnal.presentation.screens.more.MoreScreen
import com.kobby.hymnal.presentation.screens.search.GlobalSearchScreen
import com.kobby.hymnal.core.update.UpdateManager
import com.kobby.hymnal.core.update.UpdateResult
import com.kobby.hymnal.core.trace.TraceEvents
import com.kobby.hymnal.core.trace.TraceManager
import com.kobby.hymnal.core.trace.traceParams
import com.kobby.hymnal.presentation.components.UpdatePromptDialog
import com.kobby.hymnal.test.TestHymnScreen
import org.koin.compose.koinInject
import com.kobby.hymnal.theme.Shapes
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.book_open
import hymnal_cmp.composeapp.generated.resources.cathedral
import hymnal_cmp.composeapp.generated.resources.find_your_hymns
import hymnal_cmp.composeapp.generated.resources.explore_collection
import hymnal_cmp.composeapp.generated.resources.my_hymns
import hymnal_cmp.composeapp.generated.resources.cd_open
import hymnal_cmp.composeapp.generated.resources.cd_settings
import hymnal_cmp.composeapp.generated.resources.cd_search
import hymnal_cmp.composeapp.generated.resources.anglican_hymnal
import hymnal_cmp.composeapp.generated.resources.search_line
import hymnal_cmp.composeapp.generated.resources.menu_2_line
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.resources.stringResource
import com.kobby.hymnal.theme.DarkTextColor
import com.kobby.hymnal.theme.HymnalAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

class HomeScreen : Screen {
    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val updateManager: UpdateManager = koinInject()
        val traceManager: TraceManager = koinInject()
        val uriHandler = LocalUriHandler.current
        var isDeveloperMode by remember { mutableStateOf(false) }
        var updateResult by remember { mutableStateOf<UpdateResult?>(null) }

        LaunchedEffect(Unit) {
            updateResult = updateManager.checkForUpdates()
        }

        updateResult?.let { result ->
            if (result is UpdateResult.UpdateAvailable) {
                LaunchedEffect(result.latestVersion, result.isMandatory) {
                    traceManager.track(
                        TraceEvents.UPDATE_PROMPT_INTERACTION,
                        traceParams(
                            "action" to "shown",
                            "mandatory" to result.isMandatory,
                            "latest_version" to result.latestVersion
                        )
                    )
                }
                UpdatePromptDialog(
                    latestVersion = result.latestVersion,
                    isMandatory = result.isMandatory,
                    onUpdateClick = {
                        traceManager.track(
                            TraceEvents.UPDATE_PROMPT_INTERACTION,
                            traceParams(
                                "action" to "update_now",
                                "mandatory" to result.isMandatory,
                                "latest_version" to result.latestVersion
                            )
                        )
                        uriHandler.openUri(updateManager.getUpdateUrl())
                    },
                    onDismissClick = {
                        traceManager.track(
                            TraceEvents.UPDATE_PROMPT_INTERACTION,
                            traceParams(
                                "action" to "later",
                                "mandatory" to result.isMandatory,
                                "latest_version" to result.latestVersion
                            )
                        )
                        updateResult = null
                    }
                )
            }
        }

        HomeScreenContent(
            onSearchClick = {
                traceManager.track(
                    TraceEvents.HOME_NAVIGATION_CLICK,
                    traceParams("target" to "search", "screen" to "home")
                )
                navigator.push(GlobalSearchScreen())
            },
            onAncientModernClick = { 
                traceManager.track(
                    TraceEvents.HOME_NAVIGATION_CLICK,
                    traceParams("target" to "category_ancient_modern", "screen" to "home")
                )
                navigator.push(HymnListScreen(
                    category = HymnRepository.CATEGORY_ANCIENT_MODERN,
                    titleCollapsed = "Ancient & Modern",
                    titleExpanded = "Ancient\n& Modern",
                    source = "category_ancient_modern"
                ))
            },
            onSupplementaryClick = { 
                traceManager.track(
                    TraceEvents.HOME_NAVIGATION_CLICK,
                    traceParams("target" to "category_supplementary", "screen" to "home")
                )
                navigator.push(HymnListScreen(
                    category = HymnRepository.CATEGORY_SUPPLEMENTARY,
                    titleCollapsed = "Supplementary",
                    titleExpanded = "Supplementary",
                    source = "category_supplementary"
                ))
            },
            onFavoritesClick = {
                traceManager.track(
                    TraceEvents.HOME_NAVIGATION_CLICK,
                    traceParams("target" to "favorites", "screen" to "home")
                )
                navigator.push(FavoritesScreen())
            },
            onCanticleClick = { 
                traceManager.track(
                    TraceEvents.HOME_NAVIGATION_CLICK,
                    traceParams("target" to "category_canticles", "screen" to "home")
                )
                navigator.push(HymnListScreen(
                    category = HymnRepository.CATEGORY_CANTICLES,
                    titleCollapsed = "Canticles",
                    titleExpanded = "Canticles",
                    source = "category_canticles"
                ))
            },
            onPsalmsClick = {
                traceManager.track(
                    TraceEvents.HOME_NAVIGATION_CLICK,
                    traceParams("target" to "category_psalms", "screen" to "home")
                )
                navigator.push(HymnListScreen(
                    category = HymnRepository.CATEGORY_PSALMS,
                    titleCollapsed = "The Psalms",
                    titleExpanded = "The\nPsalms",
                    source = "category_psalms"
                ))
            },
            onMoreClick = {
                traceManager.track(
                    TraceEvents.HOME_NAVIGATION_CLICK,
                    traceParams("target" to "more", "screen" to "home")
                )
                navigator.push(MoreScreen())
            },
            onMoreLongClick = {
                isDeveloperMode = !isDeveloperMode
                traceManager.track(
                    TraceEvents.HOME_NAVIGATION_CLICK,
                    traceParams("target" to "developer_mode_toggle", "enabled" to isDeveloperMode, "screen" to "home")
                )
            },
            onTestDatabaseClick = { navigator.push(TestHymnScreen()) },
            isDeveloperMode = isDeveloperMode
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    onSearchClick: () -> Unit = {},
    onAncientModernClick: () -> Unit = {},
    onSupplementaryClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onCanticleClick: () -> Unit = {},
    onPsalmsClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onMoreLongClick: () -> Unit = {},
    onTestDatabaseClick: () -> Unit = {},
    isDeveloperMode: Boolean = false
) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)) {
        Image(
            painter = painterResource(Res.drawable.cathedral),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                AppBar(
                    onMoreClick = onMoreClick,
                    onMoreLongClick = onMoreLongClick,
                    onSearchClick = onSearchClick
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxWidth()
                .padding( paddingValues)
                .offset(y = (80).dp)
                .verticalScroll(rememberScrollState())

            ) {

                    ScreenBackground(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(32.dp, 32.dp, 0.dp, 0.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .padding(vertical = 20.dp)
                                .fillMaxWidth()
                        ) {

                            SemiTransparentCard {
                                Text(
                                    text = stringResource(Res.string.find_your_hymns),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = DarkTextColor,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = stringResource(Res.string.explore_collection),
                                    color = DarkTextColor.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                                )

                                Button(
                                    onClick = onFavoritesClick,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    ),
                                    shape = Shapes.medium,
                                    modifier = Modifier.height(40.dp)
                                ) {
                                    Text(
                                        text = stringResource(Res.string.my_hymns),
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Outlined.ArrowForward,
                                        contentDescription = stringResource(Res.string.cd_open),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            CategoryButtons(
                                title = "Ancient & Modern",
                                onClick = onAncientModernClick
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            CategoryButtons(
                                title = "Supplementary",
                                onClick = onSupplementaryClick
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            CategoryButtons(
                                title = "Canticles",
                                onClick = onCanticleClick
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            CategoryButtons(
                                title = "The Psalms",
                                onClick = onPsalmsClick
                            )
                            if (isDeveloperMode) {
                                Spacer(modifier = Modifier.height(12.dp))
                                CategoryButtons(
                                    title = "Test Database",
                                    onClick = onTestDatabaseClick
                                )
                            }
                        }
                    }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun AppBar(
    onMoreClick: () -> Unit = {},
    onMoreLongClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(34.dp),
                    painter = painterResource(Res.drawable.book_open),
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(Res.string.anglican_hymnal),
                    style = MaterialTheme.typography.headlineLarge,
                    color = DarkTextColor
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.Transparent
        ),
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = vectorResource(Res.drawable.search_line),
                    contentDescription = stringResource(Res.string.cd_search),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            IconButton(
                modifier = Modifier.combinedClickable(
                    onClick = onMoreClick,
                    onLongClick = onMoreLongClick
                ),
                onClick = onMoreClick
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = vectorResource(Res.drawable.menu_2_line),
                    contentDescription = stringResource(Res.string.cd_settings),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

        }
    )
}

@Preview(showBackground = true,)
@Composable
fun HomeScreenContentPreview(){
    HymnalAppTheme {
        HomeScreenContent()
    }
}
