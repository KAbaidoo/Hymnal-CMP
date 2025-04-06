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
fun AppTypography(): Typography{
    val playFairDisplay = FontFamily(Font(Res.font.play_fair_display_variable_font_wght))

    val onest = FontFamily(Font(Res.font.onest_variable_font_wght))

    return Typography(
        displayMedium =TextStyle(
            fontFamily = playFairDisplay,
            fontWeight = FontWeight.SemiBold,
            fontSize = 48.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = playFairDisplay,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp
        ),
        headlineMedium =  TextStyle(
            fontFamily = playFairDisplay,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
        ),
        headlineSmall =  TextStyle(
            fontFamily = playFairDisplay,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = onest,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = onest,
            fontWeight = FontWeight.Normal,
            lineHeight = 36.sp,
            fontSize = 16.sp
        ),
        bodySmall = TextStyle(
            fontFamily = onest,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp
        )
    )
}