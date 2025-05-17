package com.kobby.hymnal.supplementary

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kobby.hymnal.core.components.DetailScreen
import com.kobby.hymnal.theme.HymnalAppTheme


@Composable
fun SupplementaryDetailScreenContent(){
    DetailScreen(
        titleCollapsed = "Supplementary",
        titleExpanded = "Supplementary",
        onFavouriteClicked =  {},
        onFontSettingsClicked = {},
        onShareClicked = {},
        contentText= "SUPP. HYMN 9\n" +
                "\n" +
                "Father, I dare believe\n" +
                "Thee merciful and true;\n" +
                "Thou wilt my guilty soul forgive,\n" +
                "My fallen soul renew.\n" +
                "\n" +
                "Come then, for Jesus' sake,\n" +
                "And bid my heart be clean,\n" +
                "And end of all my troubles make\n" +
                "An end of all my sin.\n" +
                "\n" +
                "I will, through grace I will!\n" +
                "I do return to Thee.\n" +
                "Take, empty it, O Lord, and fill\n" +
                "My heart with purity.\n" +
                "\n" +
                "For power I feebly pray:\n" +
                "Thy kingdom now restore.\n" +
                "Today, while it is called today,\n" +
                "I shall not sin no more."
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SupplementaryDetailScreenContentPreview() {
    HymnalAppTheme {
        SupplementaryDetailScreenContent()
    }
}
