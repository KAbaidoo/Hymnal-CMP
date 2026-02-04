package com.kobby.hymnal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.theme.Shapes
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.musical_note
import org.jetbrains.compose.resources.vectorResource
import com.kobby.hymnal.theme.DarkBackground
import com.kobby.hymnal.theme.DarkTextColor

@Composable
fun CategoryButtons(
    title: String, 
    icon: ImageVector = vectorResource(Res.drawable.musical_note),
    onClick: () -> Unit = {}
){
    Button(
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
            .padding(8.dp)
            .height(64.dp),
        onClick = onClick,
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
                modifier = Modifier
                    .clip(Shapes.large)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(12.dp),
                imageVector = icon,
                tint = DarkTextColor,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                color = DarkTextColor,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}