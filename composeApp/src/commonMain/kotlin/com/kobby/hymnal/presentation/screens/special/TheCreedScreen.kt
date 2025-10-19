package com.kobby.hymnal.presentation.screens.special

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.composeApp.database.Hymn
import com.kobby.hymnal.presentation.components.DetailScreen

class TheCreedScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        val creedContent = Hymn(
            id = 0,
            number = 0,
            title = "The Apostles' Creed",
            category = "creed",
            content = "I believe in God the Father Almighty, Maker of heaven and earth:\n\n" +
                    "And in Jesus Christ his only Son our Lord, Who was conceived by the Holy Ghost, " +
                    "Born of the Virgin Mary, Suffered under Pontius Pilate, Was crucified, dead, and buried: " +
                    "He descended into hell; The third day he rose again from the dead, He ascended into heaven, " +
                    "And sitteth on the right hand of God the Father Almighty; From thence he shall come to judge the quick and the dead.\n\n" +
                    "I believe in the Holy Ghost; The holy Catholic Church; The Communion of Saints; " +
                    "The Forgiveness of sins; The Resurrection of the body, And the Life everlasting. Amen.",
            created_at = 0
        )
        
        DetailScreen(
            hymn = creedContent,
            isFavorite = false,
            onBackClick = { navigator.pop() },
            onHomeClick = { 
                // Navigate to home by popping all screens
                while (navigator.canPop) {
                    navigator.pop()
                }
            },
            showActionButtons = false
        )
    }
}