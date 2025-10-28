package com.kobby.hymnal.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
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
import com.kobby.hymnal.test.TestHymnScreen
import com.kobby.hymnal.theme.Shapes
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.book_open
import hymnal_cmp.composeapp.generated.resources.cathedral
import hymnal_cmp.composeapp.generated.resources.heart_2_line
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

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var isDeveloperMode by remember { mutableStateOf(false) }
        
        HomeScreenContent(
            onSearchClick = { navigator.push(GlobalSearchScreen()) },
            onAncientModernClick = { 
                navigator.push(HymnListScreen(
                    category = HymnRepository.CATEGORY_ANCIENT_MODERN,
                    titleCollapsed = "Ancient & Modern",
                    titleExpanded = "Ancient\n& Modern"
                ))
            },
            onSupplementaryClick = { 
                navigator.push(HymnListScreen(
                    category = HymnRepository.CATEGORY_SUPPLEMENTARY,
                    titleCollapsed = "Supplementary",
                    titleExpanded = "Supplementary"
                ))
            },
            onFavoritesClick = { navigator.push(FavoritesScreen()) },
            onCanticleClick = { 
                navigator.push(HymnListScreen(
                    category = HymnRepository.CATEGORY_CANTICLES,
                    titleCollapsed = "Canticles",
                    titleExpanded = "Canticles"
                ))
            },
            onMoreClick = { navigator.push(MoreScreen()) },
            onMoreLongClick = { isDeveloperMode = !isDeveloperMode },
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
    onMoreClick: () -> Unit = {},
    onMoreLongClick: () -> Unit = {},
    onTestDatabaseClick: () -> Unit = {},
    isDeveloperMode: Boolean = false
) {
    Box(modifier = Modifier.fillMaxSize()) {
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
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .offset(y = (100).dp)
            ) {
                ScreenBackground(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(32.dp, 32.dp, 0.dp, 0.dp))
                ) {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(16.dp)
                            .padding(vertical = 34.dp)
                            .fillMaxWidth()
                    ) {
                        SemiTransparentCard {
                            Text(
                                text = stringResource(Res.string.find_your_hymns),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = stringResource(Res.string.explore_collection),
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
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
//                        CategoryButtons(
//                            title = "Favourites",
//                            icon = vectorResource(Res.drawable.heart_2_line),
//                            onClick = onFavoritesClick
//                        )
                        
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
                    color = MaterialTheme.colorScheme.onPrimary
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