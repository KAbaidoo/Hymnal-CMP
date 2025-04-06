package com.kobby.hymnal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import com.kobby.hymnal.core.components.ContentScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val darkColor = Color.Transparent
            val lightColor = Color.Transparent

            val isDarkTheme = isSystemInDarkTheme()

            enableEdgeToEdge(
                statusBarStyle =  if (isDarkTheme)
                    SystemBarStyle.dark(darkColor.hashCode())
                 else
                    SystemBarStyle.light(lightColor.hashCode(), lightColor.hashCode())
                ,
                navigationBarStyle = if (isDarkTheme)
                    SystemBarStyle.dark(darkColor.hashCode())
                 else  SystemBarStyle.light(lightColor.hashCode(), lightColor.hashCode())
            )
//            HymnalApp()
//            ContentScreen()
        }
    }
}

