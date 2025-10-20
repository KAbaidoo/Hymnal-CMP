package com.kobby.hymnal.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.onest_variable_font_wght
import hymnal_cmp.composeapp.generated.resources.play_fair_display_variable_font_wght
import org.jetbrains.compose.resources.Font

@Composable
fun getAppFontFamily(fontName: String): FontFamily {
    return when (fontName) {
        "PlayFair Display" -> FontFamily(Font(Res.font.play_fair_display_variable_font_wght))
        "Onest" -> FontFamily(Font(Res.font.onest_variable_font_wght))
        else -> FontFamily(Font(Res.font.onest_variable_font_wght)) // Default to Onest
    }
}

@Composable
fun AppTypography(
    contentFontFamily: String = "Onest",
    contentFontSize: Float = 16f
): Typography {
    val playFairDisplay = FontFamily(Font(Res.font.play_fair_display_variable_font_wght))
    val contentFont = getAppFontFamily(contentFontFamily)

    return Typography(
        displayMedium = TextStyle(
            fontFamily = playFairDisplay,
            fontWeight = FontWeight.SemiBold,
            fontSize = 48.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = playFairDisplay,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = playFairDisplay,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = playFairDisplay,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = contentFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = contentFontSize.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = contentFont,
            fontWeight = FontWeight.Normal,
            lineHeight = (contentFontSize * 2.25f).sp,
            fontSize = contentFontSize.sp
        ),
        bodySmall = TextStyle(
            fontFamily = contentFont,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp
        )
    )
}