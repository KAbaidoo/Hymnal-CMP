package com.kobby.hymnal.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.kobby.hymnal.R
import com.kobby.hymnal.core.components.SearchTextField
import com.kobby.hymnal.theme.HymnalAppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchScreen(
    content: @Composable (innerPadding: PaddingValues)-> Unit,
    bottomBar: @Composable () -> Unit
) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
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

                        },
                        navigationIcon = {
                            IconButton(onClick = { /* TODO */ }) {
                                Icon(
                                    modifier = Modifier.size(30.dp),
                                    imageVector = ImageVector.vectorResource(R.drawable.arrow_left_s_line),
                                    contentDescription = "Back",
                                )
                            }
                        },
                        actions = {

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
                        painter = painterResource(R.drawable.book_leaf),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }



            },
            bottomBar = bottomBar
            ,
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { innerPadding ->
            content(innerPadding)
        }


}
@Preview
@Composable
fun GlobalSearchScreenPreview() {
    HymnalAppTheme {
        GlobalSearchScreen(
            content = { innerPadding ->
               Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)) {
                   SearchTextField(modifier = Modifier.fillMaxWidth(), searchText = "", onTextChanged = {}, placeholderText = "Search by number, word..")

                }
            },
            bottomBar = {

            }
        )
    }

}
