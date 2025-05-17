package com.kobby.hymnal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.kobby.hymnal.main.MainScreen
import com.kobby.hymnal.start.StartScreen
import com.kobby.hymnal.theme.HymnalAppTheme
@Composable
fun HymnalApp() {

    val useCase = remember { ShowOnboarding.INSTANCE }
    val showOnboarding by useCase.execute().collectAsState(initial = false)


        HymnalAppTheme  {
//            val statusBarValues = WindowInsets.safeDrawing.asPaddingValues()

//            Column(modifier = Modifier.fillMaxWidth().padding(top = statusBarValues.calculateTopPadding())) {
       if (showOnboarding) {
                Navigator(StartScreen()) { navigator ->
                    SlideTransition(navigator)
                }
            } else {
                Navigator(MainScreen()) { navigator ->
                    SlideTransition(navigator)
                }
            }
//            }

        }
}