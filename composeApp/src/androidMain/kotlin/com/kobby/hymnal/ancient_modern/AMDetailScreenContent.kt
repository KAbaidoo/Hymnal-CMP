package com.kobby.hymnal.ancient_modern

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kobby.hymnal.core.components.DetailScreen
import com.kobby.hymnal.theme.HymnalAppTheme


@Composable
fun AMDetailScreenContent(){

  DetailScreen(
        titleCollapsed = "A & M 207",
        titleExpanded = "A & M\n207",
        onFavouriteClicked =  {},
        onFontSettingsClicked = {},
        onShareClicked = {},
        contentText= "HYMN 207\n" +
                "\n" +
                "Our Blest Redeemer, ere He breathed His tender last farewell, A Guide, a Comforter, \n" +
                "bequeathed With us to dwell. \n" +
                "\n" +
                "He came sweet influence to impart, A gracious willing Guest, While He can find one \n" +
                "humble heart Wherein to rest. \n" +
                "\n" +
                "And His that gentle voice we hear, Soft as the breath of even, That checks each fault, that \n" +
                "calms each fear, \n" +
                "And speaks of Heav’n. \n" +
                "\n" +
                "And every virtue we possess, And every conquest won, \n" +
                "And every thought of holiness, Are His alone. \n" +
                "\n" +
                "SPIRIT of purity and grace, Our weakness, pitying, see: O make our hearts Thy dwelling \n" +
                "place, And worthier Thee."
  )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun AMDetailScreenContentPreview() {
    HymnalAppTheme {
        AMDetailScreenContent()
    }
}

