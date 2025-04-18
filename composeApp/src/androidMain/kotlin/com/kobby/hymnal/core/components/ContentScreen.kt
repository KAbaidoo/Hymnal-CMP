package com.kobby.hymnal.core.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.R
import com.kobby.hymnal.theme.HymnalAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentScreen(
    titleCollapsed:String,
    titleExpanded:String,
    actionButtons: @Composable() (() -> Unit?)? = null,
    content: @Composable (innerPadding: PaddingValues)-> Unit,
    bottomBar: @Composable () -> Unit
) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val topAppBarElementColor = MaterialTheme.colorScheme.secondary
    val isCollapsed = remember { derivedStateOf { scrollBehavior.state.collapsedFraction > 0.5 } }

    val titleText = if (isCollapsed.value) {
        titleCollapsed
    } else {
       titleExpanded
    }

    val titleStyle = if (!isCollapsed.value) {
        MaterialTheme.typography.headlineLarge

    } else {
        MaterialTheme.typography.headlineSmall
    }
    Box {

        Scaffold(
            topBar = {

                LargeTopAppBar(
                    title = {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                            Text(titleText, style = titleStyle)
                            if (actionButtons != null && !isCollapsed.value) {
                               actionButtons()
                           }
                        }
                            },
                    navigationIcon = {
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.arrow_left_s_line),
                                contentDescription = "Search",
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.home_3_line),
                                contentDescription = "Search"
                            )
                        }},
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        scrolledContainerColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = topAppBarElementColor,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor= topAppBarElementColor,
                    ),
                    scrollBehavior = scrollBehavior
                )

            },
            bottomBar = bottomBar
            ,
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { innerPadding ->
            content(innerPadding)
        }
        Image(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(250.dp),
            painter = painterResource(R.drawable.book_leaf),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }

}
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ContentPreview() {
    HymnalAppTheme {
        ContentScreen(
            titleCollapsed =  "The Creed",
            titleExpanded =  "The\nCreed",
            actionButtons = null,
            content = { innerPadding ->
                val items = List(20) { _ -> "Hello" }
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)) {

                    items(items) {
                        ListItem(title = "Item")
                    }
                }
            },
            bottomBar = { SearchTextField(modifier = Modifier.fillMaxWidth(), searchText = "", onTextChanged = {}, placeholderText = "Search by number, word..")}
        )
    }

}
