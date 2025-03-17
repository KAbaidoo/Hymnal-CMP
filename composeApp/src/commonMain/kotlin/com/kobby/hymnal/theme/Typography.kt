package com.kobby.hymnal.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import hymnal_cmp.composeapp.generated.resources.Onest_VariableFont_wght
import hymnal_cmp.composeapp.generated.resources.PlayfairDisplay_VariableFont_wght
import hymnal_cmp.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font


@Composable
fun AppTypography(): Typography{
    val playFairDisplay = FontFamily(Font(Res.font.PlayfairDisplay_VariableFont_wght))

    val onest = FontFamily(Font(Res.font.Onest_VariableFont_wght))

    return Typography(
        displayLarge =TextStyle(
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
            fontSize = 18.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = onest,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = onest,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        ),
        bodySmall = TextStyle(
            fontFamily = onest,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp
        )
    )
}