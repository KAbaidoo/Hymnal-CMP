package com.kobby.hymnal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.theme.Shapes
import com.kobby.hymnal.theme.DarkBackground

@Composable
fun SemiTransparentCard(content: @Composable () -> Unit){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Shapes.large)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground.copy(alpha = 0.6f),
                        DarkBackground.copy(alpha = 0.6f)
                    )
                )
            )
            .padding(24.dp)
    ) {
        content()
    }
}