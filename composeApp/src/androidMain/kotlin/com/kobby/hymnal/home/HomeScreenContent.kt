package com.kobby.hymnal.home

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.R
import com.kobby.hymnal.start.ScreenBackground
import com.kobby.hymnal.theme.HymnalAppTheme
import com.kobby.hymnal.theme.Shapes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenContent(
    modifier: Modifier = Modifier,
    destination: SignupDestination,
    onBackClicked: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.cathedral),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent, // Make scaffold background transparent
            topBar = {
                AppBar()
            }
        ) { paddingValues ->
            // Content with padding from the scaffold
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .offset(y = (100).dp)
            ) {
                // Your main content here
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
                        SemiTransparentCard{
                            Text(
                                text = "Find Your Hymns",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Explore our collection of Anglican hymn.",
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                            )

                            Button(
                                onClick = { /* TODO */ },
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
                        CategoryButtons(title = "Ancient & Modern")
                        Spacer(modifier = Modifier.height(12.dp))
                        CategoryButtons(title = "Supplementary")
                        Spacer(modifier = Modifier.height(12.dp))
                        CategoryButtons(title = "The Creed")
                        Spacer(modifier = Modifier.height(12.dp))
                        CategoryButtons(title = "Favourites", icon = ImageVector.vectorResource(R.drawable.heart_2_line))


                    }


                }
            }
        }
    }
}

@Preview
//@DevicePreviews
@Composable
fun MainScreenContentPreview() {
    HymnalAppTheme {
//        CategoryButtons(title = "Ancient & Modern", icon = Icons.Outlined.FavoriteBorder)
        MainScreenContent(
            modifier = Modifier,
            destination = SignupDestination.NAME
        ) {

        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
//    destination: SignupDestination
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
                    modifier = Modifier
                        .size(34.dp),
                    painter = painterResource(R.drawable.book_open),
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
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            IconButton(onClick = { /* TODO */ }) {
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
@Composable
fun SemiTransparentCard( content:@Composable () -> Unit){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Shapes.large)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                )
            )
            .padding(24.dp)
    ) {
        content()
    }
}

@Composable
fun CategoryButtons(title: String, icon: ImageVector = ImageVector.vectorResource(R.drawable.musical_note)){

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .clip(Shapes.large)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    )
                )
                .padding(8.dp)
                .height(64.dp),
            onClick = { /* TODO */ },
//            contentPadding = PaddingValues(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    modifier = Modifier.clip(Shapes.large)
                        .background(MaterialTheme.colorScheme.primary).padding(12.dp),
                    imageVector = icon,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

}
private enum class SignupDestination {
    EMAIL,
    PASSWORD,
    GENDER,
    NAME,
    ARTISTS,
    PODCASTS
}

@Composable
private fun SignupDestination.title(): String {
    return when (this) {
        SignupDestination.EMAIL,
        SignupDestination.PASSWORD,
        SignupDestination.GENDER,
        SignupDestination.NAME -> ""

        SignupDestination.ARTISTS -> ""
        SignupDestination.PODCASTS -> ""
    }
}
