package com.kobby.hymnal.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.book_leaf
import org.jetbrains.compose.resources.painterResource

@Composable
fun ScreenBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit){
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(250.dp),
            painter = painterResource(Res.drawable.book_leaf),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        content()
    }
}