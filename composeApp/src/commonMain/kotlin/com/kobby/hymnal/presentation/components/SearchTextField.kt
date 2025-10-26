package com.kobby.hymnal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.theme.Shapes

@Composable
fun SearchTextField(
    searchText: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String,
    contentPadding: PaddingValues = PaddingValues(14.dp)
) {
    val textStyle = MaterialTheme.typography.titleMedium

    TextField(
        value = searchText, 
        onValueChange = onTextChanged,
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(contentPadding), 
        placeholder = {  
            Text(
                text = placeholderText,
                style = textStyle,
            )
        },
        textStyle = textStyle,
        leadingIcon = { 
            Icon(
                imageVector = Icons.Outlined.Search, 
                contentDescription = null
            )
        }, 
        shape = Shapes.large,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.primary, 
            focusedContainerColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}