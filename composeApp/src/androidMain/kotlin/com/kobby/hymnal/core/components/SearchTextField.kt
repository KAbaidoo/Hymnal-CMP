package com.kobby.hymnal.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.theme.HymnalAppTheme
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

    TextField(value = searchText, onValueChange = onTextChanged,modifier = modifier
        .background(MaterialTheme.colorScheme.background)
        .padding(contentPadding), placeholder = {  Text(
        text = placeholderText,
        style = textStyle,
    )},textStyle = textStyle,
        leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription =null )
    }, shape = Shapes.large,
    colors = TextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.primary, unfocusedIndicatorColor = Color.Transparent)
    )


}

@Preview
@Composable
fun SearchTextFieldPreview() {
    HymnalAppTheme {
        SearchTextField(
            searchText = "",
            onTextChanged = {

            },
            placeholderText = "search by number, word..."

        )
    }

}