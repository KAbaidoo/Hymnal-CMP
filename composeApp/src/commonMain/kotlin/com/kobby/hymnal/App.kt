package com.kobby.hymnal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.LifecycleResumeEffect
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.kobby.hymnal.core.notifications.NotificationScheduler
import com.kobby.hymnal.start.StartScreen
import com.kobby.hymnal.theme.HymnalAppTheme
import org.koin.compose.koinInject

@Composable
fun HymnalApp() {
    val notificationScheduler: NotificationScheduler = koinInject()

    LaunchedEffect(Unit) {
        notificationScheduler.onAppLaunched()
    }

    LifecycleResumeEffect(Unit) {
        notificationScheduler.onAppBecameActive()
        onPauseOrDispose {}
    }

    HymnalAppTheme {
        Navigator(StartScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}
