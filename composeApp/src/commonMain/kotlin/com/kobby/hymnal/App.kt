package com.kobby.hymnal

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.kobby.hymnal.start.StartScreen
import com.kobby.hymnal.theme.HymnalAppTheme

@Composable
fun HymnalApp() {
    HymnalAppTheme {
        Navigator(StartScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}