package com.kobby.hymnal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.kobby.hymnal.core.database.DatabaseInitializer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = Color.Black.toArgb()
//        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        super.onCreate(savedInstanceState)

        Firebase.initialize(this)

        installSplashScreen()
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

            val databaseInitializer = DatabaseInitializer(this)
            HymnalApp(databaseInitializer)
        }
    }
}

