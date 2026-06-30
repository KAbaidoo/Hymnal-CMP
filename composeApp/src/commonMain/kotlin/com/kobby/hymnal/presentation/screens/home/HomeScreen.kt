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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.kobby.hymnal.presentation.components.CategoryButtons
import com.kobby.hymnal.presentation.components.ScreenBackground
import com.kobby.hymnal.presentation.components.HymnOfTheWeekCard
import com.kobby.hymnal.presentation.screens.hymns.HymnListScreen
import com.kobby.hymnal.presentation.screens.hymns.HymnDetailScreen
import com.kobby.hymnal.core.database.HymnRepository
import com.kobby.hymnal.core.config.RemoteConfigManager
import com.kobby.hymnal.core.sharing.ShareManager
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.kobby.hymnal.composeApp.database.Hymn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import hymnal_cmp.composeapp.generated.resources.arafed_cross_hill_with_foggy_sky_background_generative_ai
import hymnal_cmp.composeapp.generated.resources.easter_sunrise_service_church
import hymnal_cmp.composeapp.generated.resources.ecological_environment_growth_seedling_tree
import hymnal_cmp.composeapp.generated.resources.mountain_peaks_peeking_through_low_hanging_clouds
import hymnal_cmp.composeapp.generated.resources.trees_forest_foggy_weather
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
import hymnal_cmp.composeapp.generated.resources.hymn_of_the_week
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
        val repository: HymnRepository = koinInject()
        val remoteConfigManager: RemoteConfigManager = koinInject()
        val shareManager: ShareManager = koinInject()
        val uriHandler = LocalUriHandler.current
        val scope = rememberCoroutineScope()
        var isDeveloperMode by remember { mutableStateOf(false) }
        var updateResult by remember { mutableStateOf<UpdateResult?>(null) }
        var featuredHymn by remember { mutableStateOf<Hymn?>(null) }

        LaunchedEffect(Unit) {
            updateResult = updateManager.checkForUpdates()
            
            withContext(Dispatchers.Default) {
                try {
                    val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                    val year = now.year
                    val week = (now.dayOfYear - 1) / 7 + 1
                    val currentWeekKey = "$year-${week.toString().padStart(2, '0')}"
                    
                    val overrideMap = remoteConfigManager.getString("hymn_of_the_week_map", "")
                    var overrideId: Long? = null
                    
                    if (overrideMap.isNotEmpty()) {
                        try {
                            val map = overrideMap.split(",")
                                .map { it.split(":") }
                                .filter { it.size == 2 }
                                .associate { it[0].trim() to it[1].trim().toLongOrNull() }
                            overrideId = map[currentWeekKey]
                        } catch (e: Exception) {
                            // Map parsing failed, fallback
                        }
                    }
                    
                    val localFeaturedId = (((year * 53L + week) % 991) + 1)
                    val finalId = overrideId ?: localFeaturedId
                    featuredHymn = repository.getHymnById(finalId) ?: repository.getHymnById(localFeaturedId)
                } catch (e: Exception) {
                    // Fallback to a random hymn or nothing
                }
            }
        }
        val favoriteHymns by repository.getFavoriteHymns().collectAsState(initial = emptyList())
        val isFeaturedFavorite = remember(featuredHymn, favoriteHymns) {
            featuredHymn?.let { hymn -> favoriteHymns.any { it.id == hymn.id } } ?: false
        }

        updateResult?.let { result ->
            if (result is UpdateResult.UpdateAvailable) {
                LaunchedEffect(result.isMandatory) {
                    traceManager.track(
                        TraceEvents.UPDATE_PROMPT_INTERACTION,
                        traceParams(
                            "action" to "shown",
                            "mandatory" to result.isMandatory
                        )
                    )
                }
                UpdatePromptDialog(
                    isMandatory = result.isMandatory,
                    onUpdateClick = {
                        traceManager.track(
                            TraceEvents.UPDATE_PROMPT_INTERACTION,
                            traceParams(
                                "action" to "update_now",
                                "mandatory" to result.isMandatory
                            )
                        )
                        uriHandler.openUri(updateManager.getUpdateUrl())
                    },
                    onDismissClick = {
                        traceManager.track(
                            TraceEvents.UPDATE_PROMPT_INTERACTION,
                            traceParams(
                                "action" to "later",
                                "mandatory" to result.isMandatory
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
            isDeveloperMode = isDeveloperMode,
            featuredHymn = featuredHymn,
            isFeaturedFavorite = isFeaturedFavorite,
            onFavoriteIconClick = {
                featuredHymn?.let { hymn ->
                    scope.launch {
                        if (isFeaturedFavorite) {
                            repository.removeFromFavorites(hymn.id)
                        } else {
                            repository.addToFavorites(hymn.id)
                        }
                    }
                }
            },
            onShareIconClick = {
                featuredHymn?.let { hymn ->
                    shareManager.shareHymn(hymn)
                }
            },
            onCardClick = {
                featuredHymn?.let { hymn ->
                    traceManager.track(
                        TraceEvents.HOME_NAVIGATION_CLICK,
                        traceParams("target" to "featured_hymn", "hymn_id" to hymn.id, "screen" to "home")
                    )
                    navigator.push(HymnDetailScreen(hymnId = hymn.id, source = "featured_hymn"))
                }
            }
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
    isDeveloperMode: Boolean = false,
    featuredHymn: Hymn? = null,
    isFeaturedFavorite: Boolean = false,
    onFavoriteIconClick: () -> Unit = {},
    onShareIconClick: () -> Unit = {},
    onCardClick: () -> Unit = {}
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
            ) {

                    ScreenBackground(
                        modifier = Modifier
                            .padding(top = 40.dp)
                            .clip(RoundedCornerShape(32.dp, 32.dp, 0.dp, 0.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .padding(vertical = 12.dp)
                                .fillMaxWidth()
                        ) {
                            val backgroundImages = listOf(
                                Res.drawable.arafed_cross_hill_with_foggy_sky_background_generative_ai,
                                Res.drawable.easter_sunrise_service_church,
                                Res.drawable.ecological_environment_growth_seedling_tree,
                                Res.drawable.mountain_peaks_peeking_through_low_hanging_clouds,
                                Res.drawable.trees_forest_foggy_weather
                            )

                            val backgroundImage = remember(featuredHymn) {
                                featuredHymn?.let { hymn ->
                                    backgroundImages[(hymn.id % backgroundImages.size).toInt()]
                                } ?: Res.drawable.cathedral
                            }

                            val categoryAbbr = remember(featuredHymn) {
                                when (featuredHymn?.category) {
                                    HymnRepository.CATEGORY_ANCIENT_MODERN -> "A&M"
                                    HymnRepository.CATEGORY_SUPPLEMENTARY -> "Supp"
                                    HymnRepository.CATEGORY_PSALMS -> "Psalm"
                                    HymnRepository.CATEGORY_CANTICLES -> "Canticle"
                                    else -> "Hymn"
                                }
                            }

                            val hymnSnippet = remember(featuredHymn) {
                                featuredHymn?.let { getHymnSnippet(it) }
                                    ?: "\"Amazing grace! how sweet the sound, That saved a wretch like me! I once was lost, but now amfound; Was blind, but now I see.\"..."
                            }

                            HymnOfTheWeekCard(
                                backgroundImage = backgroundImage,
                                isFavorite = isFeaturedFavorite,
                                title = stringResource(Res.string.hymn_of_the_week),
                                hymnCategory = categoryAbbr,
                                hymnNumber = featuredHymn?.number?.toString() ?: "207",
                                hymnSnippet = hymnSnippet,
                                onFavoritesButtonClick = onFavoritesClick,
                                onFavoriteIconClick = onFavoriteIconClick,
                                onShareIconClick = onShareIconClick,
                                onCardClick = onCardClick
                            )

                            Spacer(modifier = Modifier.height(16.dp))
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

private fun getHymnSnippet(hymn: Hymn): String {
    val content = hymn.content
    val lines = content.lines().filter { it.isNotBlank() }

    return if (hymn.category == HymnRepository.CATEGORY_CANTICLES || hymn.category == HymnRepository.CATEGORY_PSALMS) {
        // For psalms and canticles, take the first two lines
        val snippet = lines.take(2).joinToString(" ")
        if (snippet.length > 150) "${snippet.substring(0, 147)}..." else "$snippet..."
    } else {
        // For standard hymns, try to extract the first verse cleanly
        val snippet = lines.take(4).joinToString(" ")
        if (snippet.length > 150) "${snippet.substring(0, 147)}..." else "$snippet..."
    }
}
