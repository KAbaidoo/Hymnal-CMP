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
import com.kobby.hymnal.presentation.screens.hymns.AncientModernListScreen
import com.kobby.hymnal.presentation.screens.hymns.SupplementaryListScreen
import com.kobby.hymnal.presentation.screens.more.FavoritesScreen
import com.kobby.hymnal.presentation.screens.search.GlobalSearchScreen
import com.kobby.hymnal.presentation.screens.special.TheCreedScreen
import com.kobby.hymnal.test.TestHymnScreen
import com.kobby.hymnal.theme.Shapes
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.book_open
import hymnal_cmp.composeapp.generated.resources.cathedral
import hymnal_cmp.composeapp.generated.resources.heart_2_line
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var isDeveloperMode by remember { mutableStateOf(false) }
        
        HomeScreenContent(
            onSearchClick = { navigator.push(GlobalSearchScreen()) },
            onAncientModernClick = { navigator.push(AncientModernListScreen()) },
            onSupplementaryClick = { navigator.push(SupplementaryListScreen()) },
            onFavoritesClick = { navigator.push(FavoritesScreen()) },
            onCreedClick = { navigator.push(TheCreedScreen()) },
            onSettingsClick = { /* TODO: Navigate to settings */ },
            onSettingsLongClick = { isDeveloperMode = !isDeveloperMode },
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
    onCreedClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onSettingsLongClick: () -> Unit = {},
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
                    onSettingsClick = onSettingsClick,
                    onSettingsLongClick = onSettingsLongClick,
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
                                text = "Find Your Hymns",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Explore our collection of Anglican hymns.",
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                            )

                            Button(
                                onClick = onAncientModernClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                shape = Shapes.medium,
                                modifier = Modifier.height(40.dp)
                            ) {
                                Text(
                                    text = "Open Hymns",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Outlined.ArrowForward,
                                    contentDescription = "Open",
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
                            title = "The Creed",
                            onClick = onCreedClick
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        CategoryButtons(
                            title = "Favourites",
                            icon = vectorResource(Res.drawable.heart_2_line),
                            onClick = onFavoritesClick
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
    onSettingsClick: () -> Unit = {},
    onSettingsLongClick: () -> Unit = {},
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
                    text = "Anglican Hymnal",
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
            IconButton(
                modifier = Modifier.combinedClickable(
                    onClick = onSettingsClick,
                    onLongClick = onSettingsLongClick
                ),
                onClick = {}
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            IconButton(onClick = onSearchClick) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    )
}