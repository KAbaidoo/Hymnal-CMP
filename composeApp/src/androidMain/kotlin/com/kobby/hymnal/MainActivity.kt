package com.kobby.hymnal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kobby.hymnal.core.crashlytics.CrashlyticsManager
import com.kobby.hymnal.core.iap.BillingHelper
import com.kobby.hymnal.core.iap.PurchaseManager
import com.kobby.hymnal.core.trace.TraceEvents
import com.kobby.hymnal.core.trace.TraceManager
import com.kobby.hymnal.core.trace.traceParams
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize subscription manager for trial tracking and entitlement state
        val purchaseManager: PurchaseManager by inject()
        purchaseManager.initialize()
        logNotificationOpenIfPresent()

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

    private fun logNotificationOpenIfPresent() {
        val category = intent?.getStringExtra("notification_category") ?: return
        val traceManager: TraceManager by inject()
        traceManager.track(
            TraceEvents.NOTIFICATION_OPENED,
            traceParams("category" to category)
        )
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
