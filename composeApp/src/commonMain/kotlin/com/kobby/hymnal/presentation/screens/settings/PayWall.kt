package com.kobby.hymnal.presentation.screens.settings

fun PayWallContent(){
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
            .background(DarkTextColor)
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
                        color = DarkTextColor
                    )
                }
            }
            Spacer(modifier = Modifier.weight(0.5f))
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(x = 28.dp, y = (-50).dp)
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
                                text = "${getCategoryAbbreviation(hymn.category)} ${hymn.number}",
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
                                text = hymn.content.take(200) + if (hymn.content.length > 200) "..." else "",
                                style = MaterialTheme.typography.bodyMedium,
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
                color = LightTextColor
            )
            Text(
                modifier = Modifier.padding(0.dp),
                text = stringResource(Res.string.author_name),
                style = MaterialTheme.typography.bodyMedium,
                color = LightTextColor
            )
        }
    }
}

@Preview(name = "PayWall Screen - Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PayWallContentPreviewLight() {
    HymnalAppTheme {
        PayWallContent(
            onStartButtonClicked = { /* Preview - no action */ },
            onRandomHymnClicked = { /* Preview - no action */ }
        )
    }
}
