package com.kobby.hymnal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.google.firebase.Firebase
import androidx.compose.runtime.Composable
import com.google.firebase.initialize
import com.kobby.hymnal.BuildKonfig
import com.kobby.hymnal.BuildConfig
import com.kobby.hymnal.core.crashlytics.CrashlyticsManager
import com.kobby.hymnal.core.iap.BillingHelper
import com.kobby.hymnal.di.androidModule
import com.kobby.hymnal.di.crashlyticsModule
import com.kobby.hymnal.di.databaseModule
import com.kobby.hymnal.di.settingsModule
import com.kobby.hymnal.di.subscriptionModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Koin
        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(databaseModule, settingsModule, androidModule, crashlyticsModule, subscriptionModule)
        }

        Firebase.initialize(this)
        
        // Set custom keys for Crashlytics context (release builds only)
        setupCrashlyticsKeys()

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

            HymnalApp()
        }
    }
    
    private fun setupCrashlyticsKeys() {
        // Get crashlytics from Koin after initialization
        val crashlytics: CrashlyticsManager by inject()
        
        // Set app version
        crashlytics.setCustomKey("app_version", BuildKonfig.VERSION_NAME)
        crashlytics.setCustomKey("version_code", BuildKonfig.VERSION_CODE)
        crashlytics.setCustomKey("build_type", if (BuildConfig.DEBUG) "debug" else "release")
        
        // Log initialization
        crashlytics.log("App initialized - version ${BuildKonfig.VERSION_NAME}")
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up billing client connection
        val billingHelper: BillingHelper by inject()
        billingHelper.endConnection()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    HymnalApp()
}

