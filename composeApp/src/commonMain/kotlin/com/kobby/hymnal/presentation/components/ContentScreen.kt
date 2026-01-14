package com.kobby.hymnal.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.arrow_left_s_line
import hymnal_cmp.composeapp.generated.resources.book_leaf
import hymnal_cmp.composeapp.generated.resources.home_3_line
import hymnal_cmp.composeapp.generated.resources.cd_back
import hymnal_cmp.composeapp.generated.resources.cd_home
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.resources.stringResource
import com.kobby.hymnal.theme.DarkTextColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentScreen(
    titleCollapsed: String,
    titleExpanded: String,
    actionButtons: @Composable() (() -> Unit?)? = null,
    content: @Composable (innerPadding: PaddingValues) -> Unit,
    bottomBar: @Composable () -> Unit = {},
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val isCollapsed = remember { derivedStateOf { scrollBehavior.state.collapsedFraction > 0.5 } }

    val titleText = if (isCollapsed.value) {
        titleCollapsed
    } else {
       titleExpanded
    }

    val titleStyle = if (!isCollapsed.value) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.headlineSmall

    val topAppBarElementColor = MaterialTheme.colorScheme.secondary
    // Define the expanded and collapsed height of the TopAppBar
    val expandedHeight = 152.dp // Default expanded height for LargeTopAppBar
    val collapsedHeight = 64.dp // Default collapsed height for LargeTopAppBar

    // Calculate the current height based on the collapsedFraction
    val currentHeight = with(LocalDensity.current) {
        lerp(
            expandedHeight.toPx(),
            collapsedHeight.toPx(),
            scrollBehavior.state.collapsedFraction
        ).toDp()
    }
    
    Scaffold(
        topBar = {
            Box {
                LargeTopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(), 
                            horizontalArrangement = Arrangement.SpaceBetween, 
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(titleText, style = titleStyle,color = DarkTextColor)
                            if (actionButtons != null && !isCollapsed.value) {
                                actionButtons()
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                imageVector = vectorResource(Res.drawable.arrow_left_s_line),
                                contentDescription = stringResource(Res.string.cd_back),
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onHomeClick) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                imageVector = vectorResource(Res.drawable.home_3_line),
                                contentDescription = stringResource(Res.string.cd_home)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        scrolledContainerColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = topAppBarElementColor,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor= topAppBarElementColor,
                    ),
                    scrollBehavior = scrollBehavior
                )
                Image(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .height(currentHeight),
                    painter = painterResource(Res.drawable.book_leaf),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        },
        bottomBar = bottomBar,
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .imePadding()
    ) { innerPadding ->
        content(innerPadding)
    }
}